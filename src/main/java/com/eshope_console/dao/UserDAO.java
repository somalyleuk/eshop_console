package com.eshope_console.dao;

import com.eshope_console.config.DatabaseConfig;
import com.eshope_console.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (id, username, email, password) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, generateUserId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user.setUserId(rs.getString("id"));
            }
            return user;
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToUser(rs);
        }
        return null;
    }

    private String generateUserId() throws SQLException {
        String sql = "SELECT id FROM users ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id");
                int nextNum = Integer.parseInt(lastId.substring(3)) + 1;
                return String.format("USR%03d", nextNum);
            } else {
                return "USR001";
            }
        }
    }

    public User findById(String userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToUser(rs);
        }
        return null;
    }

    public boolean existsByUsername(String username) throws SQLException {
        return findByUsername(username) != null;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) users.add(mapResultSetToUser(rs));
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getString("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}