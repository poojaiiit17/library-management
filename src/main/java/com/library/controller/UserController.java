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
public class UserController {
    private final LibraryService libraryService = new LibraryService();

    @GetMapping("/user-dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = getUser(session);
        if (user == null || !"USER".equals(user.getRole())) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("issuedBooks", libraryService.getIssuedBooks(user.getUserId()));
        model.addAttribute("availableBooks", libraryService.getAvailableBooks());
        return "user-dashboard";
    }

    @PostMapping("/issue-book")
    public String issueBook(@RequestParam int bookId, HttpSession session) {
        User user = getUser(session);
        if (user == null || !"USER".equals(user.getRole())) return "redirect:/login";
        boolean success = libraryService.issueBook(user.getUserId(), bookId);
        session.setAttribute("message", success ? "Book issued successfully" : "Book could not be issued. It may be unavailable or already issued by you.");
        return "redirect:/user-dashboard";
    }

    @PostMapping("/return-book")
    public String returnBook(@RequestParam int issueId, HttpSession session) {
        User user = getUser(session);
        if (user == null || !"USER".equals(user.getRole())) return "redirect:/login";
        boolean success = libraryService.returnBook(issueId, user.getUserId());
        session.setAttribute("message", success ? "Book returned successfully" : "Book could not be returned");
        return "redirect:/user-dashboard";
    }

    private User getUser(HttpSession session) { return (User) session.getAttribute("user"); }
}
