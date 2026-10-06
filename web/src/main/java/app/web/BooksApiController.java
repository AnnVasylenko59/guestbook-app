package app.web;

import app.core.domain.Book;
import app.core.service.BookService;
import app.core.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BooksApiController {

    private static final Logger log = LoggerFactory.getLogger(BooksApiController.class);

    private final BookService bookService;
    private final CommentService commentService;

    // Spring автоматично ін'єктує залежності через конструктор
    public BooksApiController(BookService bookService, CommentService commentService) {
        this.bookService = bookService;
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<?> getBooks(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Book> books = bookService.findBooks(q, page, size);
            return ResponseEntity.ok(books);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid GET /api/books: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("DB error while GET /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable long id) {
        try {
            Book book = bookService.findBook(id);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Book not found"));
            }
            return ResponseEntity.ok(book);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid GET /api/books/{}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("DB error while GET /api/books/{}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error"));
        }
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            if (book.getTitle() == null || book.getTitle().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
            }
            if (book.getAuthor() == null || book.getAuthor().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Author is required"));
            }
            if (book.getPubYear() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid pubYear"));
            }

            Book saved = bookService.addBook(
                    book.getTitle().trim(),
                    book.getAuthor().trim(),
                    book.getPubYear()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid POST /api/books: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("DB error while POST /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error"));
        }
    }

    @DeleteMapping("/{bookId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable long bookId, @PathVariable long commentId) {
        try {
            commentService.delete(bookId, commentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            log.warn("Comment deletion rejected: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid DELETE /api/books/{}/comments/{}: {}", bookId, commentId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("DB error while deleting comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error"));
        }
    }
}