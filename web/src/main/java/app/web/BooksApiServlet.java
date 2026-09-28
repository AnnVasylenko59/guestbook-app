package app.web;

import app.core.domain.Book;
import app.core.service.BookService;
import app.core.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class BooksApiServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BooksApiServlet.class);

    private final BookService bookService;
    private final CommentService commentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BooksApiServlet(BookService bookService, CommentService commentService) {
        this.bookService = bookService;
        this.commentService = commentService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonContentType(resp);
        String path = req.getPathInfo();

        try {
            if (path != null && path.length() > 1) {
                long id = parseBookId(path);
                Book book = bookService.findBook(id);

                if (book == null) {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Book not found");
                    return;
                }

                objectMapper.writeValue(resp.getWriter(), book);
                return;
            }

            int page = parseInt(req.getParameter("page"), 0);
            int size = parseInt(req.getParameter("size"), 10);
            String q = req.getParameter("q");

            var books = bookService.findBooks(q, page, size);
            objectMapper.writeValue(resp.getWriter(), books);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid GET /api/books: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("DB error while GET /api/books", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonContentType(resp);

        try {
            Book book = objectMapper.readValue(req.getInputStream(), Book.class);

            if (book.getTitle() == null || book.getTitle().isBlank()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Title is required");
                return;
            }

            if (book.getAuthor() == null || book.getAuthor().isBlank()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Author is required");
                return;
            }

            if (book.getPubYear() <= 0) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid pubYear");
                return;
            }

            Book saved = bookService.addBook(
                    book.getTitle().trim(),
                    book.getAuthor().trim(),
                    book.getPubYear()
            );

            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), saved);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid POST /api/books: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("DB error while POST /api/books", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        try {
            long[] ids = parseCommentPath(path);
            long bookId = ids[0];
            long commentId = ids[1];

            commentService.delete(bookId, commentId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (IllegalStateException e) {
            log.warn("Comment deletion rejected: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid DELETE /api/books/*: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("DB error while deleting comment", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private long parseBookId(String path) {
        String clean = path.substring(1);

        if (clean.contains("/")) {
            throw new IllegalArgumentException("Invalid book path");
        }
        try {
            return Long.parseLong(clean);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid book ID");
        }
    }

    private long[] parseCommentPath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Comment path is required");
        }

        String[] parts = path.split("/");

        // Expected: /{bookId}/comments/{commentId}
        if (parts.length != 4 || !"comments".equals(parts[2])) {
            throw new IllegalArgumentException("Expected /{bookId}/comments/{commentId}");
        }

        try {
            long bookId = Long.parseLong(parts[1]);
            long commentId = Long.parseLong(parts[3]);
            return new long[]{bookId, commentId};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid book or comment ID");
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
}