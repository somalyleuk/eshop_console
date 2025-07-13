package com.eshope_console.service;

import com.eshope_console.dao.UserDAO;
import com.eshope_console.model.User;
import com.eshope_console.util.InputValidator;
import com.eshope_console.util.PasswordUtil;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String username, String email, String password) throws Exception {
    // Validate username, email, password
    if (!InputValidator.isValidUsername(username)) {
        throw new Exception("Username must be 3-50 characters long and contain only letters, numbers, and underscores.");
    }
    if (!InputValidator.isValidPassword(password)) {
        throw new Exception("Password must be at least 6 characters long.");
    }
    if (!InputValidator.isNonEmpty(email)) {
        throw new Exception("Email cannot be empty.");
    }
    if (userDAO.existsByUsername(username)) {
        throw new Exception("Username already exists. Please choose a different username.");
    }
    String hashedPassword = PasswordUtil.hashPassword(password);
    User user = new User(username, email, hashedPassword);
    return userDAO.createUser(user);
}

    public User login(String username, String password) throws Exception {
        if (!InputValidator.isNonEmpty(username) || !InputValidator.isNonEmpty(password)) {
            throw new Exception("Username and password cannot be empty.");
        }
        User user = userDAO.findByUsername(username);
        if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new Exception("Invalid username or password.");
        }
        return user;
    }


}