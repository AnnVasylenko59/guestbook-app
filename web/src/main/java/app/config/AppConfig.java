package app.config;

import app.core.service.CommentService;
import app.persistence.jdbc.JdbcCommentRepository;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppConfig implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Збираємо залежності до купи в окремому конфігураційному пакеті
        CommentService commentService = new CommentService(new JdbcCommentRepository());

        // Зберігаємо готовий сервіс у контекст додатку
        sce.getServletContext().setAttribute("commentService", commentService);
    }
}