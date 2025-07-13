package com.eshope_console.controller;

import com.eshope_console.model.Order;
import com.eshope_console.model.Product;
import com.eshope_console.model.User;
import com.eshope_console.service.*;
import com.eshope_console.view.ConsoleView;
import com.eshope_console.view.MenuView;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ShopController {
    private final Scanner scanner;
    private final MenuView menuView;
    private final ConsoleView consoleView;
    private final AuthService authService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final ReadProductController readProductController;
    private User currentUser = null;

    public ShopController(Scanner scanner, MenuView menuView, ConsoleView consoleView,
                          AuthService authService, ProductService productService,
                          CartService cartService, OrderService orderService,
                          ReadProductController readProductController) {
        this.scanner = scanner;
        this.menuView = menuView;
        this.consoleView = consoleView;
        this.authService = authService;
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.readProductController = readProductController;
    }

    public void start() {
        consoleView.showWelcome();
        while (true) {
            try {
                if (currentUser == null) {
                    handleAuthMenu();
                } else {
                    handleMainMenu();
                }
            } catch (Exception e) {
                consoleView.showError("An unexpected error occurred: " + e.getMessage());
                menuView.pressEnterToContinue();
            }
        }
    }

    private void handleAuthMenu() {
        int choice = menuView.showAuthMenu();
        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleRegister();
            case 3 -> {
                consoleView.showMessage("Thank you for using ShopEase! Goodbye! 👋");
                System.exit(0);
            }
        }
    }

    private void handleLogin() {
        try {
            String[] credentials = menuView.getLoginCredentials();
            currentUser = authService.login(credentials[0], credentials[1]);
            consoleView.showSuccess("Welcome back, " + currentUser.getUsername() + "! 🎉");
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
            menuView.pressEnterToContinue();
        }
    }

    private void handleRegister() {
        try {
            String[] credentials = menuView.getRegisterCredentials();
            // credentials[0] = username, credentials[1] = email, credentials[2] = password
            currentUser = authService.register(credentials[0], credentials[1], credentials[2]);
            consoleView.showSuccess("Registration successful! Welcome, " + currentUser.getUsername() + "! 🎉");
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
            menuView.pressEnterToContinue();
        }
    }

    private void handleMainMenu() {
        int choice = menuView.showMainMenu();
        switch (choice) {
            case 1 -> handleViewAllProducts();
            case 2 -> handleSearchProducts();
            case 3 -> handleCartManagement();
            case 4 -> handleOrderHistory();
            case 5 -> handleReadProductOperations();
            case 6 -> handleLogout();
        }
    }

    private void handleViewAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            consoleView.showProducts(products);
            menuView.pressEnterToContinue();
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
            menuView.pressEnterToContinue();
        }
    }

    private void handleSearchProducts() {
        try {
            String keyword = menuView.getSearchKeyword();
            List<Product> products = productService.searchProducts(keyword);
            if (products.isEmpty()) {
                consoleView.showMessage("No products found matching '" + keyword + "'");
            } else {
                consoleView.showProducts(products);
            }
            menuView.pressEnterToContinue();
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
            menuView.pressEnterToContinue();
        }
    }

    private void handleCartManagement() {
        while (true) {
            int choice = menuView.showCartMenu();
            switch (choice) {
                case 1 -> handleViewCart();
                case 2 -> handleAddToCart();
                case 3 -> handleRemoveFromCart();
                case 4 -> handleUpdateQuantity();
                case 5 -> handleCheckout();
                case 6 -> {
                    return;
                }
            }
        }
    }

    private void handleViewCart() {
        try {
            Map<String, Integer> cartItems = cartService.getCartItems();
            List<Product> allProducts = productService.getAllProducts();
            double total = cartService.calculateTotal();
            consoleView.showCart(cartItems, allProducts, total);
            menuView.pressEnterToContinue();
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
            menuView.pressEnterToContinue();
        }
    }

    private void handleAddToCart() {
        try {
            String code = menuView.getProductId();
            Product product = productService.getProductByCode(code);
            if (product == null) {
                consoleView.showError("Product not found with code: " + code);
                menuView.pressEnterToContinue();
                return;
            }
            int quantity = menuView.getQuantity();
            cartService.addToCart(product.getProductCode(), quantity);
            consoleView.showSuccess("Added " + quantity + " × " + product.getProductName() + " to cart!");
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
        }
        menuView.pressEnterToContinue();
    }

    private void handleRemoveFromCart() {
        try {
            String code = menuView.getProductId();
            Product product = productService.getProductByCode(code);
            if (product == null) {
                consoleView.showError("Product not found with code: " + code);
                menuView.pressEnterToContinue();
                return;
            }
            cartService.removeFromCart(product.getProductCode());
            consoleView.showSuccess("Removed " + product.getProductName() + " from cart!");
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
        }
        menuView.pressEnterToContinue();
    }

    private void handleUpdateQuantity() {
        try {
            String code = menuView.getProductId();
            Product product = productService.getProductByCode(code);
            if (product == null) {
                consoleView.showError("Product not found with code: " + code);
                menuView.pressEnterToContinue();
                return;
            }
            int quantity = menuView.getQuantity();
            cartService.updateQuantity(product.getProductCode(), quantity);
            consoleView.showSuccess("Updated quantity for " + product.getProductName() + " to " + quantity);
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
        }
        menuView.pressEnterToContinue();
    }

    private void handleCheckout() {
        try {
            if (cartService.isEmpty()) {
                consoleView.showWarning("Your cart is empty!");
                menuView.pressEnterToContinue();
                return;
            }
            double total = cartService.calculateTotal();
            if (menuView.confirmCheckout(total)) {
                Map<String, Integer> cartItems = cartService.getCartItems();
                Order order = orderService.createOrder(currentUser.getUserId(), cartItems);
                cartService.clearCart();
                consoleView.showSuccess("Order placed successfully! Order code: " + order.getOrderCode());
            } else {
                consoleView.showMessage("Checkout cancelled.");
            }
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
        }
        menuView.pressEnterToContinue();
    }

    private void handleOrderHistory() {
        try {
            List<Order> orders = orderService.getUserOrders(currentUser.getUserId());
            consoleView.showOrderHistory(orders);
            menuView.pressEnterToContinue();
        } catch (Exception e) {
            consoleView.showError(e.getMessage());
            menuView.pressEnterToContinue();
        }
    }

    private void handleReadProductOperations() {
        readProductController.showReadProductMenu();
    }

    private void handleLogout() {
        currentUser = null;
        consoleView.showSuccess("Logged out successfully! See you next time! 👋");
    }
}