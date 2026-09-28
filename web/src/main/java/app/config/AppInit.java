package app.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import app.persistence.jdbc.DbInit;
import app.web.AppStatsService;

@SpringBootApplication(scanBasePackages = {"app"})
public class AppInit {
    public static void main(String[] args) {
        DbInit.init();

        ApplicationContext context = SpringApplication.run(AppInit.class, args);

        System.out.println("\n--------------------------------------------------");
        System.out.println("ПЕРЕВІРКА ЗАВАНТАЖЕННЯ ПАРАМЕТРІВ ТА БІНІВ:");

        AppStatsService statsService = context.getBean(AppStatsService.class);

        // Вивід параметрів з properties
        statsService.printCustomProperties();

        // Вивід роботи ін'єкцій
        System.out.println(statsService.getQuickStatsSummary());
        System.out.println("--------------------------------------------------\n");

        System.out.println("Started at http://localhost:8080/books");
    }
}