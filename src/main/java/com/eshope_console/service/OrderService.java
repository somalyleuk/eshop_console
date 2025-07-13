package com.eshope_console.service;

import com.eshope_console.dao.OrderDAO;
import com.eshope_console.model.Order;
import com.eshope_console.model.OrderItem;
import com.eshope_console.model.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderDAO orderDAO;
    private final ProductService productService;

    public OrderService(OrderDAO orderDAO, ProductService productService) {
        this.orderDAO = orderDAO;
        this.productService = productService;
    }

    public Order createOrder(String userId, Map<String, Integer> cartItems) throws Exception {
        if (cartItems.isEmpty()) throw new Exception("Cart is empty.");
        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0.0;
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            Product product = productService.getProductByCode(entry.getKey());
            if (product == null) throw new Exception("Product not found: " + entry.getKey());
            if (entry.getValue() > product.getStockQuantity()) {
                throw new Exception("Not enough stock for " + product.getProductName() + ". Available: " + product.getStockQuantity());
            }
            OrderItem item = new OrderItem();
            item.setProductId(product.getProductId());
            item.setProductCode(product.getProductCode());
            item.setProductName(product.getProductName());
            item.setQuantity(entry.getValue());
            item.setPricePerItem(product.getPrice());
            orderItems.add(item);
            totalPrice += item.getTotalPrice();
        }
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);
        Order savedOrder = orderDAO.createOrder(order);
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            Product product = productService.getProductByCode(entry.getKey());
            int newStock = product.getStockQuantity() - entry.getValue();
            productService.updateProductStock(product.getProductCode(), newStock);
        }
        return savedOrder;
    }

    public List<Order> getUserOrders(String userId) throws Exception {
        return orderDAO.findByUserId(userId);
    }


}