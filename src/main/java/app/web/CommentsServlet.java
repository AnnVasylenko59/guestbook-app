package app.web;

import app.db.CommentDao;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@MultipartConfig // Необхідно для коректного парсингу FormData з frontend
public class CommentsServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);
    private final CommentDao dao = new CommentDao();
    private final com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/json; charset=UTF-8");
            var list = dao.list();
            om.writeValue(resp.getWriter(), list);
        } catch (Exception e) {
            log.error("DB error", e);
            resp.sendError(500, "DB error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String author = req.getParameter("author");
            String text = req.getParameter("text");

            if (author == null || author.trim().isEmpty() || author.length() > 64 ||
                    text == null || text.trim().isEmpty() || text.length() > 1000) {
                resp.sendError(400, "Bad Request: invalid validation");
                return;
            }

            dao.add(author.trim(), text.trim());
            log.info("New comment added: author='{}', length={}", author.trim(), text.trim().length());

            resp.setStatus(204);
        } catch (Exception e) {
            log.error("DB error", e);
            resp.sendError(500, "DB error");
        }
    }
}