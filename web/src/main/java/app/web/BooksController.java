package app.web;

import io.javalin.Javalin;
import io.javalin.http.Context;
import app.core.port.CatalogRepositoryPort;
import app.core.port.CommentRepositoryPort;
import app.core.domain.PageRequest;

import java.util.Map;

public class BooksController {
    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public BooksController(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/books", this::getBooks);
        app.get("/api/books/{id}", this::getBook);

        // Маршрути для коментарів
        app.get("/api/comments", this::getComments);
        app.post("/api/comments", this::addComment);
        app.delete("/api/books/{bookId}/comments/{id}", this::deleteComment);
        app.post("/api/books", this::addBook);
    }

    private void addBook(Context ctx) {
        BookRequest req = ctx.bodyAsClass(BookRequest.class);
        bookRepo.add(req.title(), req.author(), req.pubYear());
        ctx.status(201);
    }

    public record BookRequest(String title, String author, int pubYear) {}

    private void getBooks(Context ctx) {
        String q = ctx.queryParam("q");
        if (q == null) {
            q = "";
        }
        var page = bookRepo.search(q, new PageRequest(0, 20));
        ctx.json(page.getItems());
    }

    private void getBook(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        var book = bookRepo.findById(id);
        if (book != null) {
            ctx.json(book);
        } else {
            ctx.status(404).json(Map.of("error", "Book not found"));
        }
    }

    private void getComments(Context ctx) {
        String bookIdParam = ctx.queryParam("bookId");
        if (bookIdParam != null && !bookIdParam.isBlank()) {
            long bookId = Long.parseLong(bookIdParam);
            // Виклик методу list з параметрами за замовчуванням
            var page = commentRepo.list(bookId, null, null, new PageRequest(0, 50));
            ctx.json(page.getItems());
        } else {
            ctx.status(400).json(Map.of("error", "Query parameter 'bookId' is required"));
        }
    }

    private void addComment(Context ctx) {
        CommentRequest req = ctx.bodyAsClass(CommentRequest.class);
        commentRepo.add(req.bookId(), req.author(), req.text());
        ctx.status(201);
    }

    private void deleteComment(Context ctx) {
        long bookId = Long.parseLong(ctx.pathParam("bookId"));
        long commentId = Long.parseLong(ctx.pathParam("id"));
        commentRepo.delete(bookId, commentId);
        ctx.status(204);
    }

    public record CommentRequest(long bookId, String author, String text) {}
}