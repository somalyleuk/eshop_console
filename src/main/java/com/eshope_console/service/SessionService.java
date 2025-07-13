package com.eshope_console.service;

import com.eshope_console.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SessionService {
    private static User currentUser = null;
    private static final String SESSION_FILE = "session.dat";

    public static void login(User user) {
        currentUser = user;
        saveSession();
    }

    public static void logout() {
        currentUser = null;
        clearSession();
    }

    public static User getCurrentUser() {
        if (currentUser == null) {
            loadSession();
        }
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    private static void saveSession() {
        if (currentUser == null) return;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            oos.writeObject(currentUser.getUserId());
            oos.writeObject(currentUser.getUsername());
        } catch (IOException e) {
            System.err.println("Warning: Could not save session: " + e.getMessage());
        }
    }

    private static void loadSession() {
        try {
            if (!Files.exists(Paths.get(SESSION_FILE))) {
                return;
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SESSION_FILE))) {
                String userId = (String) ois.readObject();
                String username = (String) ois.readObject();

                currentUser = new User();
                currentUser.setUserId(userId);
                currentUser.setUsername(username);
            }
        } catch (IOException | ClassNotFoundException e) {
           
            System.err.println("Warning: Could not load session: " + e.getMessage());
        }
        clearSession();
    }

    private static void clearSession() {
        try {
            Files.deleteIfExists(Paths.get(SESSION_FILE));
        } catch (IOException e) {
            System.err.println("Warning: Could not delete session file: " + e.getMessage());
        }
    }
}