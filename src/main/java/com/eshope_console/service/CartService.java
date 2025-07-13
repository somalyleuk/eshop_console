package com.eshope_console.service;

import com.eshope_console.model.Product;

import java.util.HashMap;
import java.util.Map;

public class CartService {
    private final Map<String, Integer> cartItems = new HashMap<>();
    private final ProductService productService;

    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public void addToCart(String productCode, int quantity) throws Exception {
        if (quantity <= 0) throw new Exception("Quantity must be greater than 0.");
        Product product = productService.getProductByCode(productCode);
        if (product == null) throw new Exception("Product not found.");
        int currentQuantity = cartItems.getOrDefault(productCode, 0);
        int newQuantity = currentQuantity + quantity;
        if (newQuantity > product.getStockQuantity()) {
            throw new Exception("Not enough stock available. Available: " + product.getStockQuantity());
        }
        cartItems.put(productCode, newQuantity);
    }

    public void removeFromCart(String productCode) {
        cartItems.remove(productCode);
    }

    public void updateQuantity(String productCode, int quantity) throws Exception {
        if (quantity <= 0) {
            removeFromCart(productCode);
            return;
        }
        Product product = productService.getProductByCode(productCode);
        if (product == null) throw new Exception("Product not found.");
        if (quantity > product.getStockQuantity()) {
            throw new Exception("Not enough stock available. Available: " + product.getStockQuantity());
        }
        cartItems.put(productCode, quantity);
    }

    public Map<String, Integer> getCartItems() {
        return new HashMap<>(cartItems);
    }

    public double calculateTotal() throws Exception {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            Product product = productService.getProductByCode(entry.getKey());
            if (product != null) {
                total += product.getPrice() * entry.getValue();
            }
        }
        return total;
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public void clearCart() {
        cartItems.clear();
    }
}