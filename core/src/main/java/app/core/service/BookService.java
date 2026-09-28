package app.core.service;

import app.core.domain.Book;
import app.core.domain.PageRequest;
import app.core.port.CatalogRepositoryPort;

import java.util.List;

public class BookService {

    private final CatalogRepositoryPort catalogRepository;

    public BookService(CatalogRepositoryPort catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public List<Book> findBooks(String query, int page, int size) {
        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        return catalogRepository
                .search(query, new PageRequest(page, size))
                .getItems();
    }

    public Book findBook(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid book ID");
        }

        return catalogRepository.findById(id);
    }

    public Book addBook(String title, String author, int pubYear) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author is required");
        }

        if (pubYear <= 0) {
            throw new IllegalArgumentException("Invalid publication year");
        }

        return catalogRepository.add(title.trim(), author.trim(), pubYear);
    }
}