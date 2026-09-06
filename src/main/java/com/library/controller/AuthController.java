package com.library.controller;

import com.library.model.User;
import com.library.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UserService userService = new UserService();

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session, Model model) {
        User user = userService.login(email, password);
        if (user == null) {
            model.addAttribute("message", "Invalid email or password");
            return "login";
        }
        session.setAttribute("user", user);
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin-dashboard";
        }
        return "redirect:/user-dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
