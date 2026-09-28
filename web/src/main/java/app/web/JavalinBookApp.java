package app.web;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.persistence.jdbc.DbInit;
import app.persistence.jdbc.JdbcBookRepository;
import app.persistence.jdbc.JdbcCommentRepository;

import java.util.Map;

public class JavalinBookApp {
    private static final Logger log = LoggerFactory.getLogger(JavalinBookApp.class);

    public static void main(String[] args) {
        // Ініціалізація схеми бази даних (створення таблиць)
        DbInit.init();

        var bookRepo = new JdbcBookRepository();
        var commentRepo = new JdbcCommentRepository();

        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, true));
        });

        // Middleware для логування запитів
        app.before(ctx -> log.info("Request: {} {}", ctx.method(), ctx.path()));

        // Реєстрація маршрутів контролера
        new BooksController(bookRepo, commentRepo).registerRoutes(app);

        // Централізована обробка винятків
        app.exception(Exception.class, (e, ctx) -> {
            log.error("Request processing error", e);
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        });

        app.start(8080);
        log.info("Javalin server started at http://localhost:8080");
    }
}