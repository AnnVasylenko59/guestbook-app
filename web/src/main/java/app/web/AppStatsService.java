package app.web;

import app.core.port.CatalogRepositoryPort;
import app.core.port.CommentRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppStatsService {

    @Autowired
    private CatalogRepositoryPort catalogRepository;

    @Autowired
    private CommentRepositoryPort commentRepository;

    @Value("${app.catalog.default-page-size:10}")
    private int defaultPageSize;

    @Value("${app.catalog.welcome-banner}")
    private String welcomeBanner;

    @Value("${app.catalog.feature-enabled:false}")
    private boolean featureEnabled;

    public String getQuickStatsSummary() {
        // Виправлено: тип змінної змінено на long
        long booksTotal = catalogRepository.search(null, new app.core.domain.PageRequest(0, defaultPageSize)).getTotal();
        return "Впроваджено через @Autowired у поле: репозиторій доступний, розмір сторінки з properties = "
                + defaultPageSize + ", книг у базі = " + booksTotal;
    }

    public void printCustomProperties() {
        System.out.println(">>> [application.properties] Баннер: " + welcomeBanner);
        System.out.println(">>> [application.properties] Default Page Size: " + defaultPageSize);
        System.out.println(">>> [application.properties] Feature Enabled: " + featureEnabled);
    }
}