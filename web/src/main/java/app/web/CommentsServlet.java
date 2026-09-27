package app.web;

import app.core.domain.Book;
import app.core.domain.Comment;
import app.core.domain.PageRequest;
import app.core.port.CatalogRepositoryPort;
import app.core.port.CommentRepositoryPort;
import app.config.Beans;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class CommentsServlet extends HttpServlet {
    private final CatalogRepositoryPort bookRepo = Beans.getBookRepo();
    private final CommentRepositoryPort commentRepo = Beans.getCommentRepo();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String bookIdStr = req.getParameter("bookId");
        if (bookIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/books");
            return;
        }

        long bookId = Long.parseLong(bookIdStr);
        try {
            Book book = bookRepo.findById(bookId);
            PageRequest pageRequest = new PageRequest(0, 20);
            List<Comment> comments = commentRepo
                    .list(bookId, null, null, pageRequest)
                    .getItems();

            req.setAttribute("book", book);
            req.setAttribute("comments", comments);
            req.getRequestDispatcher("/WEB-INF/views/book-comments.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Cannot load book details", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String method = req.getParameter("_method");

        // Хак для обробки видалення з простої HTML-форми
        if ("delete".equalsIgnoreCase(method)) {
            long bookId = Long.parseLong(req.getParameter("bookId"));
            long commentId = Long.parseLong(req.getParameter("commentId"));
            try {
                commentRepo.delete(bookId, commentId);
                resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
            } catch (Exception e) {
                throw new ServletException("Cannot delete comment", e);
            }
            return;
        }

        long bookId = Long.parseLong(req.getParameter("bookId"));
        String author = req.getParameter("author");
        String text = req.getParameter("text");

        if (author == null || author.isBlank() || text == null || text.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "author & text required");
            return;
        }

        try {
            commentRepo.add(bookId, author.trim(), text.trim());
            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
        } catch (Exception e) {
            throw new ServletException("Cannot save comment", e);
        }
    }
}