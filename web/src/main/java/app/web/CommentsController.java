package app.web;

import app.core.domain.Book;
import app.core.domain.Comment;
import app.core.port.CatalogRepositoryPort;
import app.core.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentsController {

    private final CatalogRepositoryPort bookRepo;
    private final CommentService commentService;

    // Spring автоматично ін'єктує залежності[cite: 6]
    public CommentsController(CatalogRepositoryPort bookRepo, CommentService commentService) {
        this.bookRepo = bookRepo;
        this.commentService = commentService;
    }

    @GetMapping
    public String getComments(@RequestParam(required = false) Long bookId, Model model) {
        if (bookId == null) {
            return "redirect:/books";
        }

        Book book = bookRepo.findById(bookId);
        if (book == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

        List<Comment> comments = commentService.findComments(bookId, null, null, 0, 20).getItems();

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);

        return "book-comments"; // Spring шукатиме /WEB-INF/views/book-comments.jsp[cite: 6]
    }

    @PostMapping
    public String addComment(
            @RequestParam long bookId,
            @RequestParam String author,
            @RequestParam String text) {
        try {
            commentService.addComment(bookId, author, text);
            return "redirect:/comments?bookId=" + bookId;

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot save comment", e);
        }
    }

    // Spring автоматично перехопить POST-запити, де є параметр _method=delete
    @PostMapping(params = "_method=delete")
    public String deleteComment(
            @RequestParam long bookId,
            @RequestParam long commentId) {
        try {
            commentService.delete(bookId, commentId);
            return "redirect:/comments?bookId=" + bookId;

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot delete comment", e);
        }
    }
}