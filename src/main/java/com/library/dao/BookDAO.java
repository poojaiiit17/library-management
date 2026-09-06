package com.library.dao;

import com.library.model.Book;
import com.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> getAllBooks() {
        return getBooks("SELECT * FROM books ORDER BY title");
    }

    public List<Book> getAvailableBooks() {
        return getBooks("SELECT * FROM books WHERE available_copies > 0 ORDER BY title");
    }

    private List<Book> getBooks(String sql) {
        List<Book> books = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(mapBook(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return books;
    }

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books(title, author, category, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getTotalCopies());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean removeBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ? AND available_copies = total_copies " +
                     "AND NOT EXISTS (SELECT 1 FROM issued_books WHERE book_id = ? AND return_date IS NULL)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean issueBook(int userId, int bookId) {
        String check = "SELECT available_copies FROM books WHERE book_id = ? FOR UPDATE";
        String duplicate = "SELECT issue_id FROM issued_books WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
        String insert = "INSERT INTO issued_books(user_id, book_id, issue_date) VALUES (?, ?, CURRENT_DATE)";
        String update = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(check)) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt("available_copies") <= 0) { con.rollback(); return false; }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(duplicate)) {
                ps.setInt(1, userId); ps.setInt(2, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { con.rollback(); return false; }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(insert)) {
                ps.setInt(1, userId); ps.setInt(2, bookId); ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setInt(1, bookId); ps.executeUpdate();
            }
            con.commit();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<com.library.model.Issue> getIssuedBooks(int userId) {
        List<com.library.model.Issue> issues = new ArrayList<>();
        String sql = "SELECT i.issue_id, i.book_id, b.title, i.user_id, i.issue_date, i.return_date " +
                     "FROM issued_books i JOIN books b ON i.book_id = b.book_id " +
                     "WHERE i.user_id = ? AND i.return_date IS NULL ORDER BY i.issue_date DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    com.library.model.Issue issue = new com.library.model.Issue();
                    issue.setIssueId(rs.getInt("issue_id"));
                    issue.setBookId(rs.getInt("book_id"));
                    issue.setBookTitle(rs.getString("title"));
                    issue.setUserId(rs.getInt("user_id"));
                    issue.setIssueDate(rs.getDate("issue_date"));
                    issue.setReturnDate(rs.getDate("return_date"));
                    issues.add(issue);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return issues;
    }

    public boolean returnBook(int issueId, int userId) {
        String find = "SELECT book_id FROM issued_books WHERE issue_id = ? AND user_id = ? AND return_date IS NULL FOR UPDATE";
        String mark = "UPDATE issued_books SET return_date = CURRENT_DATE WHERE issue_id = ? AND user_id = ? AND return_date IS NULL";
        String update = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            int bookId;
            try (PreparedStatement ps = con.prepareStatement(find)) {
                ps.setInt(1, issueId); ps.setInt(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { con.rollback(); return false; }
                    bookId = rs.getInt("book_id");
                }
            }
            try (PreparedStatement ps = con.prepareStatement(mark)) {
                ps.setInt(1, issueId); ps.setInt(2, userId);
                if (ps.executeUpdate() == 0) { con.rollback(); return false; }
            }
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setInt(1, bookId); ps.executeUpdate();
            }
            con.commit();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        return new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                rs.getString("category"), rs.getInt("total_copies"), rs.getInt("available_copies"));
    }
}
