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
        return catalogRepository.search(query, new PageRequest(page, size)).getItems();
    }

    public Book addBook(String title, String author, int pubYear) {
        return catalogRepository.add(title, author, pubYear);
    }
}