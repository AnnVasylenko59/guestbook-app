package app.persistence.jdbc;

import app.core.domain.Comment;
import app.core.domain.Page;
import app.core.domain.PageRequest;
import app.core.port.CommentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

public class JdbcCommentRepository implements CommentRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(JdbcCommentRepository.class);

    @Override
    public void add(long bookId, String author, String text) {
        String sql = "insert into comments (book_id, author, text) values (?, ?, ?)";

        try (var c = Db.get();
             var ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, bookId);
            ps.setString(2, author);
            ps.setString(3, text);
            ps.executeUpdate();

            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    log.info("DB: new comment #{}, book={}, author='{}', len={}", id, bookId, author, text.length());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB insert error", e);
        }
    }

    @Override
    public Page<Comment> list(long bookId, String author, Instant since, PageRequest request) {
        var items = new ArrayList<Comment>();
        StringBuilder sql = new StringBuilder("select id, author, text, created_at from comments where book_id = ?");

        if (author != null && !author.isBlank()) {
            sql.append(" and lower(author) like ?");
        }

        if (since != null) {
            sql.append(" and created_at >= ?");
        }

        sql.append(" order by created_at desc limit ? offset ?");

        try (var c = Db.get();
             var ps = c.prepareStatement(sql.toString())) {

            int i = 1;
            ps.setLong(i++, bookId);

            if (author != null && !author.isBlank()) {
                ps.setString(i++, "%" + author.trim().toLowerCase() + "%");
            }

            if (since != null) {
                ps.setTimestamp(i++, Timestamp.from(since));
            }

            ps.setInt(i++, request.getSize());
            ps.setInt(i, request.getPage() * request.getSize());

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Comment(
                            rs.getLong("id"),
                            bookId,
                            rs.getString("author"),
                            rs.getString("text"),
                            rs.getTimestamp("created_at").toInstant()
                    ));
                }
            }

            long total = count(c, bookId, author, since);
            return new Page<>(items, request, total);

        } catch (SQLException e) {
            throw new RuntimeException("DB query error", e);
        }
    }

    private long count(Connection c, long bookId, String author, Instant since) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) from comments where book_id = ?");

        if (author != null && !author.isBlank()) {
            sql.append(" and lower(author) like ?");
        }

        if (since != null) {
            sql.append(" and created_at >= ?");
        }

        try (var ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setLong(i++, bookId);

            if (author != null && !author.isBlank()) {
                ps.setString(i++, "%" + author.trim().toLowerCase() + "%");
            }
            if (since != null) {
                ps.setTimestamp(i++, Timestamp.from(since));
            }

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    @Override
    public Comment findById(long bookId, long commentId) {
        String sql = "select id, author, text, created_at from comments where id = ? and book_id = ?";

        try (var c = Db.get();
             var ps = c.prepareStatement(sql)) {

            ps.setLong(1, commentId);
            ps.setLong(2, bookId);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Comment(
                            rs.getLong("id"),
                            bookId,
                            rs.getString("author"),
                            rs.getString("text"),
                            rs.getTimestamp("created_at").toInstant()
                    );
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("DB query error", e);
        }
    }

    @Override
    public void delete(long bookId, long commentId) {
        String sql = "delete from comments where id = ? and book_id = ?";

        try (var c = Db.get();
             var ps = c.prepareStatement(sql)) {

            ps.setLong(1, commentId);
            ps.setLong(2, bookId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                log.info("DB: deleted comment #{}, book={}", commentId, bookId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB delete error", e);
        }
    }
}