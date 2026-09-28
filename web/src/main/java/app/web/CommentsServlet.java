package app.web;

import app.core.domain.Book;
import app.core.domain.Comment;
import app.core.port.CatalogRepositoryPort;
import app.core.service.CommentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class CommentsServlet extends HttpServlet {

    private final CatalogRepositoryPort bookRepo;
    private final CommentService commentService;

    public CommentsServlet(CatalogRepositoryPort bookRepo, CommentService commentService) {
        this.bookRepo = bookRepo;
        this.commentService = commentService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bookIdStr = req.getParameter("bookId");

        if (bookIdStr == null || bookIdStr.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/books");
            return;
        }

        long bookId;

        try {
            bookId = Long.parseLong(bookIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid bookId");
            return;
        }

        try {
            Book book = bookRepo.findById(bookId);

            if (book == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                return;
            }

            List<Comment> comments = commentService
                    .findComments(bookId, null, null, 0, 20)
                    .getItems();

            req.setAttribute("book", book);
            req.setAttribute("comments", comments);

            req.getRequestDispatcher("/WEB-INF/views/book-comments.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Cannot load book comments", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String method = req.getParameter("_method");

        if ("delete".equalsIgnoreCase(method)) {
            deleteComment(req, resp);
            return;
        }

        addComment(req, resp);
    }

    private void addComment(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String bookIdStr = req.getParameter("bookId");
        String author = req.getParameter("author");
        String text = req.getParameter("text");

        long bookId;

        try {
            bookId = Long.parseLong(bookIdStr);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid bookId");
            return;
        }

        try {
            commentService.addComment(bookId, author, text);
            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ServletException("Cannot save comment", e);
        }
    }

    private void deleteComment(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String bookIdStr = req.getParameter("bookId");
        String commentIdStr = req.getParameter("commentId");

        long bookId;
        long commentId;

        try {
            bookId = Long.parseLong(bookIdStr);
            commentId = Long.parseLong(commentIdStr);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid bookId or commentId");
            return;
        }

        try {
            commentService.delete(bookId, commentId);
            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            throw new ServletException("Cannot delete comment", e);
        }
    }
}