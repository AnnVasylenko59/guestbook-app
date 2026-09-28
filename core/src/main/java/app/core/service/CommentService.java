package app.core.service;

import app.core.domain.Comment;
import app.core.domain.Page;
import app.core.domain.PageRequest;
import app.core.port.CommentRepositoryPort;

import java.time.Duration;
import java.time.Instant;

public class CommentService {

    private static final Duration DELETE_WINDOW =
            Duration.ofHours(24);

    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    public void addComment(
            long bookId,
            String author,
            String text
    ) {
        if (bookId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid bookId"
            );
        }

        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException(
                    "Author is required"
            );
        }

        if (author.trim().length() > 64) {
            throw new IllegalArgumentException(
                    "Author must be at most 64 characters"
            );
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Text is required"
            );
        }

        if (text.trim().length() > 1000) {
            throw new IllegalArgumentException(
                    "Text must be at most 1000 characters"
            );
        }

        repo.add(
                bookId,
                author.trim(),
                text.trim()
        );
    }

    public Page<Comment> findComments(
            long bookId,
            String author,
            Instant since,
            int page,
            int size
    ) {
        if (bookId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid bookId"
            );
        }

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }

        return repo.list(
                bookId,
                author,
                since,
                new PageRequest(page, size)
        );
    }

    /**
     * Видалити коментар можна тільки протягом
     * 24 годин після його створення.
     */
    public void delete(
            long bookId,
            long commentId
    ) {
        Comment comment =
                repo.findById(
                        bookId,
                        commentId
                );

        if (comment == null) {
            throw new IllegalArgumentException(
                    "Comment not found"
            );
        }

        Duration age =
                Duration.between(
                        comment.getCreatedAt(),
                        Instant.now()
                );

        if (age.isNegative()) {
            throw new IllegalStateException(
                    "Comment creation time is in the future"
            );
        }

        if (age.compareTo(DELETE_WINDOW) > 0) {
            throw new IllegalStateException(
                    "Comment can only be deleted within 24 hours"
            );
        }

        repo.delete(
                bookId,
                commentId
        );
    }
}