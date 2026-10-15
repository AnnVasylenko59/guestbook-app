package app.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Додає в модель "langBase" — поточний URL без параметра lang, який закінчується
 * на "?" або "&". Завдяки цьому перемикач мови в header не губить q / bookId.
 */
@ControllerAdvice
public class LangLinksAdvice {

    @ModelAttribute("langBase")
    public String langBase(HttpServletRequest request) {
        String query = request.getQueryString();
        String rest = query == null ? "" : query.replaceAll("(^|&)lang=[^&]*", "");
        if (rest.startsWith("&")) {
            rest = rest.substring(1);
        }
        return request.getRequestURI() + "?" + (rest.isEmpty() ? "" : rest + "&");
    }
}
