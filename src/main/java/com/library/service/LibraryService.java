package com.library.service;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.Issue;

import java.util.List;

public class LibraryService {
    private final BookDAO bookDAO = new BookDAO();

    public List<Book> getAllBooks() { return bookDAO.getAllBooks(); }
    public List<Book> getAvailableBooks() { return bookDAO.getAvailableBooks(); }
    public List<Issue> getIssuedBooks(int userId) { return bookDAO.getIssuedBooks(userId); }

    public boolean issueBook(int userId, int bookId) {
        return userId > 0 && bookId > 0 && bookDAO.issueBook(userId, bookId);
    }

    public boolean returnBook(int issueId, int userId) {
        return issueId > 0 && userId > 0 && bookDAO.returnBook(issueId, userId);
    }

    public boolean addBook(String title, String author, String category, int copies) {
        if (title == null || title.isBlank() || author == null || author.isBlank() || copies <= 0) return false;
        return bookDAO.addBook(new Book(0, title.trim(), author.trim(),
                category == null ? "General" : category.trim(), copies, copies));
    }

    public boolean removeBook(int bookId) {
        return bookId > 0 && bookDAO.removeBook(bookId);
    }
}
