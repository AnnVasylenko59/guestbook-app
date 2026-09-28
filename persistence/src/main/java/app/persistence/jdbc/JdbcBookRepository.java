package app.persistence.jdbc;

import app.core.domain.Book;
import app.core.domain.Page;
import app.core.domain.PageRequest;
import app.core.port.CatalogRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class JdbcBookRepository implements CatalogRepositoryPort {

    @Override
    public Page<Book> search(String q, PageRequest request) {
        var items = new ArrayList<Book>();
        StringBuilder sql = new StringBuilder("SELECT id, title, author, pub_year FROM books WHERE 1=1");

        if (q != null && !q.isBlank()) {
            sql.append(" AND (LOWER(title) LIKE ? OR LOWER(author) LIKE ?)");
        }

        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            int i = 1;

            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                ps.setString(i++, pattern);
                ps.setString(i++, pattern);
            }

            ps.setInt(i++, request.getSize());
            ps.setInt(i, request.getPage() * request.getSize());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("pub_year")
                    ));
                }
            }

            long total = countBooks(c, q);
            return new Page<>(items, request, total);

        } catch (SQLException e) {
            throw new RuntimeException("Database error while searching books", e);
        }
    }

    private long countBooks(Connection c, String q) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM books WHERE 1=1");

        if (q != null && !q.isBlank()) {
            sql.append(" AND (LOWER(title) LIKE ? OR LOWER(author) LIKE ?)");
        }

        try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    @Override
    public Book findById(long id) {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement("SELECT id, title, author, pub_year FROM books WHERE id = ?")) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("pub_year")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while finding book", e);
        }
    }

    @Override
    public Book add(String title, String author, int pubYear) {
        String sql = "INSERT INTO books (title, author, pub_year) VALUES (?, ?, ?)";

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, pubYear);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Book(id, title, author, pubYear);
                }
            }

            throw new RuntimeException("Book was inserted but ID was not generated");

        } catch (SQLException e) {
            throw new RuntimeException("Database error while adding book", e);
        }
    }
}