package app.config;

import app.core.port.CatalogRepositoryPort;
import app.core.port.CommentRepositoryPort;
import app.core.service.CommentService;
import app.persistence.jdbc.DbInit;
import app.persistence.jdbc.JdbcBookRepository;
import app.persistence.jdbc.JdbcCommentRepository;

public class Beans {
    private static CommentService commentService;
    private static final CatalogRepositoryPort bookRepo = new JdbcBookRepository();
    private static final CommentRepositoryPort commentRepo = new JdbcCommentRepository();

    public static void init() {
        DbInit.init();
        commentService = new CommentService(commentRepo);
    }

    public static CatalogRepositoryPort getBookRepo() {
        return bookRepo;
    }

    public static CommentRepositoryPort getCommentRepo() {
        return commentRepo;
    }

    public static CommentService getCommentService() {
        return commentService;
    }
}