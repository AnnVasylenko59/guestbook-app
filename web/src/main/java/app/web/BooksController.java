package app.web;

import app.core.domain.Book;
import app.core.domain.PageRequest;
import app.core.port.CatalogRepositoryPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final CatalogRepositoryPort bookRepo;

    public BooksController(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    @GetMapping
    public String getBooks(@RequestParam(name = "q", required = false) String query, Model model) {
        List<Book> books = bookRepo.search(query, new PageRequest(0, 20)).getItems();
        model.addAttribute("books", books);
        model.addAttribute("q", query);
        return "books";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("book") Book book, BindingResult result) {
        // typeMismatch (нечислове значення року) уже лежить у result
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            result.rejectValue("title", "book.title.required");
        }
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            result.rejectValue("author", "book.author.required");
        }
        if (!result.hasFieldErrors("pubYear")
                && (book.getPubYear() < 1000 || book.getPubYear() > Year.now().getValue())) {
            result.rejectValue("pubYear", "book.pubYear.range");
        }
        if (result.hasErrors()) {
            return "book-form";
        }

        // !!! Підставте свій метод збереження з CatalogRepositoryPort / BookService
        bookRepo.add(book.getTitle().trim(), book.getAuthor().trim(), book.getPubYear());

        return "redirect:/books";
    }
}
