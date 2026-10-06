package app.web;

import app.core.domain.Comment;
import app.core.domain.Page;
import app.core.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentsApiController {

    private static final Logger log = LoggerFactory.getLogger(CommentsApiController.class);

    private final CommentService commentService;

    // Spring автоматично ін'єктує CommentService[cite: 8]
    public CommentsApiController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<?> getComments(
            @RequestParam long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String sinceParam) {

        try {
            Instant since = null;
            if (sinceParam != null && !sinceParam.isBlank()) {
                since = Instant.parse(sinceParam);
            }

            Page<Comment> result = commentService.findComments(bookId, author, since, page, size);
            return ResponseEntity.ok(result); // Spring сам конвертує об'єкт у JSON[cite: 8]

        } catch (DateTimeParseException | IllegalArgumentException e) {
            log.warn("Invalid GET /api/comments request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error while GET /api/comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error"));
        }
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentRequest body) {
        try {
            commentService.addComment(body.bookId, body.author, body.text);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Comment created"));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid POST /api/comments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error while POST /api/comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error"));
        }
    }

    public static class CommentRequest {
        public long bookId;
        public String author;
        public String text;

        public CommentRequest() {}
    }
}