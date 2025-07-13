package com.eshope_console.dao;

import com.eshope_console.config.DatabaseConfig;
import com.eshope_console.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public Product findByCode(String code) throws SQLException {
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToProductWithCategory(rs);
        }
        return null;
    }

    public List<Product> findAllWithCategories() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id ORDER BY c.name, p.name";
        try (Connection conn = DatabaseConfig.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) products.add(mapResultSetToProductWithCategory(rs));
        }
        return products;
    }

    public List<Product> searchByNameOrCategory(String keyword) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE LOWER(p.name) LIKE ? OR LOWER(c.name) LIKE ? ORDER BY c.name, p.name";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) products.add(mapResultSetToProductWithCategory(rs));
        }
        return products;
    }

    public boolean updateStock(String productCode, int newStock) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStock);
            stmt.setString(2, productCode);
            return stmt.executeUpdate() > 0;
        }
    }

    private Product mapResultSetToProductWithCategory(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getString("id"));
        product.setProductCode(rs.getString("id"));
        product.setProductName(rs.getString("name"));
        product.setCategoryId(rs.getString("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        product.setPrice(rs.getDouble("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return product;
    }
}