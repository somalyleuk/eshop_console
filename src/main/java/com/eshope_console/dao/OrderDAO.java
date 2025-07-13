package com.eshope_console.dao;

import com.eshope_console.config.DatabaseConfig;
import com.eshope_console.model.Order;
import com.eshope_console.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public Order createOrder(Order order) throws SQLException {
        String orderSql = "INSERT INTO orders (id, user_id, total_amount, created_at) VALUES (?, ?, ?, ?) RETURNING id";
        String itemSql = "INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String orderId;
                try (PreparedStatement stmt = conn.prepareStatement(orderSql)) {
                    stmt.setString(1, generateOrderId());
                    stmt.setString(2, order.getUserId());
                    stmt.setDouble(3, order.getTotalPrice());
                    stmt.setTimestamp(4, Timestamp.valueOf(order.getOrderDate()));
                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    orderId = rs.getString("id");
                    order.setOrderId(orderId);
                    order.setOrderCode(orderId);
                }
                try (PreparedStatement stmt = conn.prepareStatement(itemSql)) {
                    for (OrderItem item : order.getOrderItems()) {
                        stmt.setString(1, generateOrderItemId());
                        stmt.setString(2, order.getOrderId());
                        stmt.setString(3, item.getProductId());
                        stmt.setInt(4, item.getQuantity());
                        stmt.setDouble(5, item.getPricePerItem());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
                conn.commit();
                return order;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Order> findByUserId(String userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setOrderItems(findOrderItems(conn, order.getOrderId()));
                orders.add(order);
            }
        }
        return orders;
    }

    private String generateOrderId() throws SQLException {
        String sql = "SELECT id FROM orders ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id");
                int nextNum = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("O%03d", nextNum);
            } else {
                return "O001";
            }
        }
    }

    private String generateOrderItemId() throws SQLException {
        String sql = "SELECT id FROM order_items ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id");
                int nextNum = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("OI%03d", nextNum);
            } else {
                return "OI001";
            }
        }
    }

    private List<OrderItem> findOrderItems(Connection conn, String orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.id as product_code, p.name as product_name FROM order_items oi LEFT JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getString("id"));
                item.setOrderId(rs.getString("order_id"));
                item.setProductId(rs.getString("product_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPricePerItem(rs.getDouble("price"));
                items.add(item);
            }
        }
        return items;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getString("id"));
        order.setOrderCode(rs.getString("id"));
        order.setUserId(rs.getString("user_id"));
        order.setOrderDate(rs.getTimestamp("created_at").toLocalDateTime());
        order.setTotalPrice(rs.getDouble("total_amount"));
        return order;
    }
}