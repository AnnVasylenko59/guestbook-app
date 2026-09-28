package app.config;

import app.core.port.CatalogRepositoryPort;
import app.core.port.CommentRepositoryPort;
import app.core.service.BookService;
import app.core.service.CommentService;
import app.persistence.jdbc.JdbcBookRepository;
import app.persistence.jdbc.JdbcCommentRepository;
import app.web.BooksApiServlet;
import app.web.BooksServlet;
import app.web.CommentsApiServlet;
import app.web.CommentsServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    // =========================
    // REPOSITORIES
    // =========================

    @Bean
    public CatalogRepositoryPort catalogRepository() {
        return new JdbcBookRepository();
    }

    @Bean
    public CommentRepositoryPort commentRepository() {
        return new JdbcCommentRepository();
    }


    // =========================
    // SERVICES
    // =========================

    @Bean
    public BookService bookService(
            CatalogRepositoryPort catalogRepository) {

        return new BookService(catalogRepository);
    }

    @Bean
    public CommentService commentService(
            CommentRepositoryPort commentRepository) {

        return new CommentService(commentRepository);
    }


    // =========================
    // HTML SERVLETS
    // =========================

    @Bean
    public ServletRegistrationBean<BooksServlet> booksServlet(
            CatalogRepositoryPort bookRepo) {

        ServletRegistrationBean<BooksServlet> registration =
                new ServletRegistrationBean<>(
                        new BooksServlet(bookRepo),
                        "/books",
                        "/books/*"
                );

        registration.setName("booksServlet");
        registration.setLoadOnStartup(1);

        return registration;
    }


    @Bean
    public ServletRegistrationBean<CommentsServlet> commentsServlet(
            CatalogRepositoryPort bookRepo,
            CommentService commentService) {

        ServletRegistrationBean<CommentsServlet> registration =
                new ServletRegistrationBean<>(
                        new CommentsServlet(
                                bookRepo,
                                commentService
                        ),
                        "/comments",
                        "/comments/*"
                );

        registration.setName("commentsServlet");
        registration.setLoadOnStartup(1);

        return registration;
    }


    // =========================
    // REST API SERVLETS
    // =========================

    @Bean
    public ServletRegistrationBean<BooksApiServlet> booksApiServlet(
            BookService bookService,
            CommentService commentService) {

        ServletRegistrationBean<BooksApiServlet> registration =
                new ServletRegistrationBean<>(
                        new BooksApiServlet(
                                bookService,
                                commentService
                        ),
                        "/api/books",
                        "/api/books/*"
                );

        registration.setName("booksApiServlet");
        registration.setLoadOnStartup(1);

        return registration;
    }


    @Bean
    public ServletRegistrationBean<CommentsApiServlet> commentsApiServlet(
            CommentService commentService) {

        ServletRegistrationBean<CommentsApiServlet> registration =
                new ServletRegistrationBean<>(
                        new CommentsApiServlet(commentService),
                        "/api/comments",
                        "/api/comments/*"
                );

        registration.setName("commentsApiServlet");
        registration.setLoadOnStartup(1);

        return registration;
    }
}