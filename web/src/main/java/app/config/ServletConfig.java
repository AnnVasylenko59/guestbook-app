package app.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import app.core.port.CatalogRepositoryPort;
import app.core.port.CommentRepositoryPort;
import app.core.service.BookService;
import app.persistence.jdbc.JdbcBookRepository;
import app.persistence.jdbc.JdbcCommentRepository;
import app.web.BooksApiServlet;
import app.web.BooksServlet;
import app.web.CommentsServlet;

@Configuration
public class ServletConfig {

    // 1. Біни репозиторіїв
    @Bean
    public CatalogRepositoryPort catalogRepository() {
        return new JdbcBookRepository();
    }

    @Bean
    public CommentRepositoryPort commentRepository() {
        return new JdbcCommentRepository();
    }

    // 2. Бін сервісу з модуля core
    @Bean
    public BookService bookService(CatalogRepositoryPort catalogRepository) {
        return new BookService(catalogRepository);
    }

    // 3. Реєстрація сервлетів
    @Bean
    public ServletRegistrationBean<BooksServlet> booksServlet(CatalogRepositoryPort bookRepo) {
        return new ServletRegistrationBean<>(new BooksServlet(bookRepo), "/books", "/books/*");
    }

    @Bean
    public ServletRegistrationBean<BooksApiServlet> booksApiServlet(BookService bookService) {
        return new ServletRegistrationBean<>(new BooksApiServlet(bookService), "/api/books", "/api/books/*");
    }

    @Bean
    public ServletRegistrationBean<CommentsServlet> commentsServlet(
            CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        return new ServletRegistrationBean<>(new CommentsServlet(bookRepo, commentRepo), "/comments", "/comments/*");
    }
}