package app.config;

import app.core.port.CatalogRepositoryPort;
import app.core.port.CommentRepositoryPort;
import app.core.service.BookService;
import app.core.service.CommentService;
import app.persistence.jdbc.JdbcBookRepository;
import app.persistence.jdbc.JdbcCommentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CatalogRepositoryPort catalogRepository() {
        return new JdbcBookRepository();
    }

    @Bean
    public CommentRepositoryPort commentRepository() {
        return new JdbcCommentRepository();
    }

    @Bean
    public BookService bookService(CatalogRepositoryPort catalogRepository) {
        return new BookService(catalogRepository);
    }

    @Bean
    public CommentService commentService(CommentRepositoryPort commentRepository) {
        return new CommentService(commentRepository);
    }
}