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

    /**
     * Insert a single product
     */
    public Product insertProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (id, name, description, price, stock_quantity, category_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductId());
            stmt.setString(2, product.getProductName());
            stmt.setString(3, product.getDescription());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStockQuantity());
            stmt.setString(6, product.getCategoryId());
            stmt.setTimestamp(7, Timestamp.valueOf(product.getCreatedAt()));
            stmt.executeUpdate();
            return product;
        }
    }

    /**
     * Bulk insert products using batch processing
     */
    public int bulkInsertProducts(List<Product> products) throws SQLException {
        if (products.isEmpty()) return 0;
        
        String sql = "INSERT INTO products (id, name, description, price, stock_quantity, category_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int batchSize = 1000; // Optimal batch size for PostgreSQL
        int totalInserted = 0;
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    stmt.setString(1, product.getProductId());
                    stmt.setString(2, product.getProductName());
                    stmt.setString(3, product.getDescription());
                    stmt.setDouble(4, product.getPrice());
                    stmt.setInt(5, product.getStockQuantity());
                    stmt.setString(6, product.getCategoryId());
                    stmt.setTimestamp(7, Timestamp.valueOf(product.getCreatedAt()));
                    stmt.addBatch();
                    
                    // Execute batch when batch size is reached or at the end
                    if ((i + 1) % batchSize == 0 || i == products.size() - 1) {
                        int[] results = stmt.executeBatch();
                        for (int result : results) {
                            if (result >= 0) totalInserted += result;
                        }
                        System.out.println("Inserted batch: " + (i + 1) + "/" + products.size() + " products");
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return totalInserted;
    }

    /**
     * Get products with pagination for large datasets
     */
    public List<Product> findProductsWithPagination(int page, int pageSize) throws SQLException {
        List<Product> products = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "ORDER BY p.id " +
                    "LIMIT ? OFFSET ?";
                    
        try (Connection conn = DatabaseConfig.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProductWithCategory(rs));
            }
        }
        return products;
    }

    /**
     * Get total count of products
     */
    public long getTotalProductCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DatabaseConfig.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    /**
     * Search products with pagination
     */
    public List<Product> searchProductsWithPagination(String keyword, int page, int pageSize) throws SQLException {
        List<Product> products = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "WHERE LOWER(p.name) LIKE ? OR LOWER(c.name) LIKE ? " +
                    "ORDER BY p.id " +
                    "LIMIT ? OFFSET ?";
                    
        try (Connection conn = DatabaseConfig.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setInt(3, pageSize);
            stmt.setInt(4, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProductWithCategory(rs));
            }
        }
        return products;
    }

    /**
     * Get products by category with pagination
     */
    public List<Product> findProductsByCategoryWithPagination(String categoryId, int page, int pageSize) throws SQLException {
        List<Product> products = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "WHERE p.category_id = ? " +
                    "ORDER BY p.id " +
                    "LIMIT ? OFFSET ?";
                    
        try (Connection conn = DatabaseConfig.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryId);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProductWithCategory(rs));
            }
        }
        return products;
    }

    /**
     * Get products with price range and pagination
     */
    public List<Product> findProductsByPriceRange(double minPrice, double maxPrice, int page, int pageSize) throws SQLException {
        List<Product> products = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "WHERE p.price BETWEEN ? AND ? " +
                    "ORDER BY p.price " +
                    "LIMIT ? OFFSET ?";
                    
        try (Connection conn = DatabaseConfig.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            stmt.setInt(3, pageSize);
            stmt.setInt(4, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProductWithCategory(rs));
            }
        }
        return products;
    }

    /**
     * Bulk update stock quantities
     */
    public int bulkUpdateStock(List<String> productCodes, List<Integer> newStocks) throws SQLException {
        if (productCodes.size() != newStocks.size()) {
            throw new IllegalArgumentException("Product codes and stock quantities must have the same size");
        }
        
        String sql = "UPDATE products SET stock_quantity = ? WHERE id = ?";
        int batchSize = 1000;
        int totalUpdated = 0;
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                for (int i = 0; i < productCodes.size(); i++) {
                    stmt.setInt(1, newStocks.get(i));
                    stmt.setString(2, productCodes.get(i));
                    stmt.addBatch();
                    
                    if ((i + 1) % batchSize == 0 || i == productCodes.size() - 1) {
                        int[] results = stmt.executeBatch();
                        for (int result : results) {
                            if (result >= 0) totalUpdated += result;
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return totalUpdated;
    }

    /**
     * Truncate the products table (delete all products, reset for bulk insert)
     */
    public void truncateProductsTable() throws SQLException {
        String sql = "TRUNCATE TABLE products CASCADE";
        try (Connection conn = DatabaseConfig.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private Product mapResultSetToProductWithCategory(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getString("id"));
        product.setProductCode(rs.getString("id"));
        product.setProductName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategoryId(rs.getString("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        product.setPrice(rs.getDouble("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return product;
    }
}