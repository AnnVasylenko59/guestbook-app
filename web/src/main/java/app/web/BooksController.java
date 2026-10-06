package app.web;

import app.core.domain.Book;
import app.core.domain.PageRequest;
import app.core.port.CatalogRepositoryPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final CatalogRepositoryPort bookRepo;

    // Spring автоматично ін'єктує залежність[cite: 6]
    public BooksController(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    @GetMapping
    public String getBooks(
            @RequestParam(name = "q", required = false) String query,
            Model model) {

        System.out.println(">>> BooksController.getBooks() CALLED");

        try {
            PageRequest pageRequest = new PageRequest(0, 20);

            List<Book> books = bookRepo
                    .search(query, pageRequest)
                    .getItems();

            // Передаємо дані у представлення (JSP)
            model.addAttribute("books", books);

            // Повертаємо логічну назву View[cite: 7]
            return "books";

        } catch (Exception e) {
            throw new RuntimeException("Cannot load books", e);
        }
    }
}