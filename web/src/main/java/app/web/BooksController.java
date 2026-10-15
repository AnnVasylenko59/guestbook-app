package app.web;

import app.core.domain.Book;
import app.core.domain.PageRequest;
import app.core.port.CatalogRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(BooksController.class);

    private final CatalogRepositoryPort bookRepo;
    private final MailService mailService;

    public BooksController(CatalogRepositoryPort bookRepo, MailService mailService) {
        this.bookRepo = bookRepo;
        this.mailService = mailService;
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

        Book saved = bookRepo.add(book.getTitle().trim(), book.getAuthor().trim(), book.getPubYear());

        try {
            mailService.sendNewBookEmail(saved);
        } catch (RuntimeException e) {
            // книга вже збережена; збій SMTP не має ламати форму
            log.error("Не вдалося надіслати лист про нову книгу", e);
        }

        return "redirect:/books";
    }
}
