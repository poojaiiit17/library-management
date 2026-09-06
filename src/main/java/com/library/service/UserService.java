package com.library.service;

import com.library.dao.UserDAO;
import com.library.model.User;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) return null;
        return userDAO.login(email.trim(), password);
    }
}
