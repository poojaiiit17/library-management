package com.library.controller;

import com.library.model.User;
import com.library.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
    private final LibraryService libraryService = new LibraryService();

    @GetMapping("/admin-dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("books", libraryService.getAllBooks());
        return "admin-dashboard";
    }

    @PostMapping("/admin/add-book")
    public String addBook(@RequestParam String title, @RequestParam String author,
                          @RequestParam(required = false, defaultValue = "General") String category,
                          @RequestParam int copies, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        boolean success = libraryService.addBook(title, author, category, copies);
        session.setAttribute("message", success ? "Book added successfully" : "Book could not be added");
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/remove-book")
    public String removeBook(@RequestParam int bookId, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        boolean success = libraryService.removeBook(bookId);
        session.setAttribute("message", success ? "Book removed successfully" : "Book cannot be removed while copies are issued");
        return "redirect:/admin-dashboard";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
}
