package app.web;

import app.core.domain.Comment;
import app.core.domain.Page;
import app.core.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

public class CommentsApiServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CommentsApiServlet.class);

    private final CommentService commentService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public CommentsApiServlet(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonContentType(resp);

        try {
            long bookId = parseRequiredLong(req.getParameter("bookId"), "bookId");
            int page = parseInt(req.getParameter("page"), 0);
            int size = parseInt(req.getParameter("size"), 20);
            String author = req.getParameter("author");
            String sinceParam = req.getParameter("since");

            Instant since = null;
            if (sinceParam != null && !sinceParam.isBlank()) {
                since = Instant.parse(sinceParam);
            }

            Page<Comment> result = commentService.findComments(bookId, author, since, page, size);
            objectMapper.writeValue(resp.getWriter(), result);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid GET /api/comments request: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error while GET /api/comments", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonContentType(resp);

        try {
            CommentRequest body = objectMapper.readValue(req.getInputStream(), CommentRequest.class);
            commentService.addComment(body.bookId, body.author, body.text);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", "Comment created"));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid POST /api/comments: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error while POST /api/comments", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private long parseRequiredLong(String value, String parameter) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(parameter + " is required");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + parameter);
        }
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + value);
        }
    }

    private void setJsonContentType(HttpServletResponse resp) {
        resp.setContentType("application/json; charset=UTF-8");
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(
                resp.getWriter(),
                Map.of("error", message == null ? "Unknown error" : message)
        );
    }

    public static class CommentRequest {
        public long bookId;
        public String author;
        public String text;

        public CommentRequest() {}
    }
}