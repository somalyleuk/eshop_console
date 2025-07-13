package com.eshope_console.view;

import com.eshope_console.model.*;
import com.eshope_console.util.ConsoleColors;

import java.util.List;
import java.util.Map;

public class ConsoleView {
    public void showWelcome() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n🛒 Welcome to ShopEase Console E-Commerce System!" + ConsoleColors.RESET);
    }

    public void showProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "\nNo products found." + ConsoleColors.RESET);
            return;
        }
        System.out.println(ConsoleColors.CYAN_BOLD + "\nPRODUCTS CATALOG" + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.BLUE_BOLD + "%-6s %-30s %-15s %-10s %-8s\n" + ConsoleColors.RESET,
                "Code", "Name", "Category", "Price", "Stock");
        System.out.println(ConsoleColors.BLUE + "-".repeat(75) + ConsoleColors.RESET);
        for (Product p : products) {
            System.out.printf("%-6s %-30s %-15s $%-9.2f %-8d\n",
                    p.getProductCode(), p.getProductName(), p.getCategoryName(), p.getPrice(), p.getStockQuantity());
        }
    }

    public void showCart(Map<String, Integer> cartItems, List<Product> products, double total) {
        System.out.println(ConsoleColors.CYAN_BOLD + "\nYOUR SHOPPING CART" + ConsoleColors.RESET);
        if (cartItems.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "Your cart is empty. Start shopping!" + ConsoleColors.RESET);
            return;
        }
        System.out.printf(ConsoleColors.BLUE_BOLD + "%-6s %-30s %-8s %-10s %-10s\n" + ConsoleColors.RESET,
                "Code", "Name", "Qty", "Unit Price", "Total");
        System.out.println(ConsoleColors.BLUE + "-".repeat(70) + ConsoleColors.RESET);
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            Product p = products.stream().filter(prod -> prod.getProductCode().equals(entry.getKey())).findFirst().orElse(null);
            if (p != null) {
                double itemTotal = p.getPrice() * entry.getValue();
                System.out.printf("%-6s %-30s %-8d $%-9.2f $%-9.2f\n",
                        p.getProductCode(), p.getProductName(), entry.getValue(), p.getPrice(), itemTotal);
            }
        }
        System.out.println(ConsoleColors.GREEN_BOLD + "TOTAL: $" + String.format("%.2f", total) + ConsoleColors.RESET);
    }

    public void showOrderHistory(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "\nNo orders found." + ConsoleColors.RESET);
            return;
        }
        System.out.println(ConsoleColors.CYAN_BOLD + "\nORDER HISTORY" + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.BLUE_BOLD + "%-6s %-20s %-10s %-10s\n" + ConsoleColors.RESET,
                "Code", "Date", "Items", "Total");
        System.out.println(ConsoleColors.BLUE + "-".repeat(55) + ConsoleColors.RESET);
        for (Order o : orders) {
            System.out.printf("%-6s %-20s %-10d $%-9.2f\n",
                    o.getOrderCode(), o.getOrderDate(), o.getTotalItems(), o.getTotalPrice());
        }
    }

    public void showMessage(String message) {
        System.out.println(ConsoleColors.CYAN + message + ConsoleColors.RESET);
    }

    public void showSuccess(String message) {
        System.out.println(ConsoleColors.GREEN_BOLD + message + ConsoleColors.RESET);
    }

    public void showError(String message) {
        System.out.println(ConsoleColors.RED_BOLD + message + ConsoleColors.RESET);
    }

    public void showWarning(String message) {
        System.out.println(ConsoleColors.YELLOW_BOLD + message + ConsoleColors.RESET);
    }
}