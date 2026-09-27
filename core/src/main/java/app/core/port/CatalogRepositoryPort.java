package app.core.port;

import app.core.domain.Book;
import app.core.domain.Page;
import app.core.domain.PageRequest;

public interface CatalogRepositoryPort {
    Page<Book> search(String query, PageRequest request);
    Book findById(long id);
    Book add(String title, String author, int pubYear);
}