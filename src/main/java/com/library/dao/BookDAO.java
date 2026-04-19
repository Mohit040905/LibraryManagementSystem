package com.library.dao;

import com.library.model.Book;
import com.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Book operations.
 * All database interactions for books go through this class.
 * Uses PreparedStatements to prevent SQL injection.
 */
public class BookDAO {

    private final Connection conn;

    public BookDAO() {
        this.conn = DBConnection.getConnection();
    }

    // ── ADD ──────────────────────────────────────────────────────────────────

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, genre, total_copies, available_copies, added_on) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());
            ps.setDate(6, Date.valueOf(book.getAddedOn()));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) book.setBookId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] addBook: " + e.getMessage());
        }
        return false;
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] getAllBooks: " + e.getMessage());
        }
        return books;
    }

    // ── SEARCH BY TITLE ───────────────────────────────────────────────────────

    public List<Book> searchByTitle(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] searchByTitle: " + e.getMessage());
        }
        return books;
    }

    // ── SEARCH BY AUTHOR ──────────────────────────────────────────────────────

    public List<Book> searchByAuthor(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author LIKE ? ORDER BY title";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] searchByAuthor: " + e.getMessage());
        }
        return books;
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] getBookById: " + e.getMessage());
        }
        return null;
    }

    // ── UPDATE AVAILABLE COPIES (called on issue/return) ─────────────────────

    public boolean updateAvailableCopies(int bookId, int delta) {
        String sql = "UPDATE books SET available_copies = available_copies + ? WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] updateAvailableCopies: " + e.getMessage());
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] deleteBook: " + e.getMessage());
        }
        return false;
    }

    // ── PRIVATE HELPER ────────────────────────────────────────────────────────

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("book_id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("genre"),
            rs.getInt("total_copies"),
            rs.getInt("available_copies"),
            rs.getDate("added_on").toLocalDate()
        );
    }
}
