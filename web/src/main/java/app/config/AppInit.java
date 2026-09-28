package app.config;

import app.persistence.jdbc.DbInit;
import app.web.AppStatsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"app"})
public class AppInit extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application) {

        return application.sources(AppInit.class);
    }

    public static void main(String[] args) {

        DbInit.init();

        ApplicationContext context =
                SpringApplication.run(AppInit.class, args);

        System.out.println("\n--------------------------------------------------");
        System.out.println("ПЕРЕВІРКА ЗАВАНТАЖЕННЯ ПАРАМЕТРІВ ТА БІНІВ:");

        AppStatsService statsService =
                context.getBean(AppStatsService.class);

        statsService.printCustomProperties();

        System.out.println(
                statsService.getQuickStatsSummary()
        );

        System.out.println("--------------------------------------------------\n");

        System.out.println(
                "Started at http://localhost:8080/books"
        );
    }
}