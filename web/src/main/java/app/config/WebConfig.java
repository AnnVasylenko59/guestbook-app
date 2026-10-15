package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Без @EnableWebMvc: Spring Boot сам налаштовує Thymeleaf і роздачу
 * статики з classpath:/static/. Тут лише локалізація.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * defaultLocale не задаємо: доки користувач не обрав мову через ?lang=,
     * береться Accept-Language браузера. Вибір зберігається в сесії.
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        registry.addInterceptor(interceptor);
    }
}
