package com.eshope_console.view;

import com.eshope_console.util.InputValidator;

import java.util.Scanner;

public class MenuView {
    private final Scanner scanner;

    public MenuView(Scanner scanner) {
        this.scanner = scanner;
    }

    public int showAuthMenu() {
        System.out.println("\n========================================");
        System.out.println("🔐 AUTHENTICATION");
        System.out.println("========================================");
        System.out.println("1. 🔑 Login");
        System.out.println("2. 📝 Register");
        System.out.println("3. 🚪 Exit");
        System.out.println("========================================");
        return InputValidator.getValidInt(scanner, "Choose an option: ", 1, 3);
    }

    public int showMainMenu() {
        System.out.println("\n========================================");
        System.out.println("🏠 MAIN MENU");
        System.out.println("========================================");
        System.out.println("1. 📦 View All Products");
        System.out.println("2. 🔍 Search Products");
        System.out.println("3. 🛒 Cart Management");
        System.out.println("4. 📋 Order History");
        System.out.println("5. 🚪 Logout");
        System.out.println("========================================");
        return InputValidator.getValidInt(scanner, "Choose an option: ", 1, 5);
    }

    public int showCartMenu() {
        System.out.println("\n========================================");
        System.out.println("🛒 CART MANAGEMENT");
        System.out.println("========================================");
        System.out.println("1. 👀 View Cart");
        System.out.println("2. ➕ Add Product to Cart");
        System.out.println("3. ➖ Remove Product from Cart");
        System.out.println("4. ✏️  Update Quantity");
        System.out.println("5. 💳 Checkout");
        System.out.println("6. ⬅️  Back to Main Menu");
        System.out.println("========================================");
        return InputValidator.getValidInt(scanner, "Choose an option: ", 1, 6);
    }

    public String[] getLoginCredentials() {
        System.out.println("\n🔑 LOGIN");
        String username = InputValidator.getNonEmptyString(scanner, "Username: ");
        String password = InputValidator.getNonEmptyString(scanner, "Password: ");
        return new String[]{username, password};
    }

    public String[] getRegisterCredentials() {
        System.out.println("\n📝 REGISTER");
        String username = InputValidator.getNonEmptyString(scanner, "Username: ");
        String email = InputValidator.getNonEmptyString(scanner, "Email: ");
        String password = InputValidator.getNonEmptyString(scanner, "Password: ");
        return new String[]{username, email, password};
    }

    public String getSearchKeyword() {
        return InputValidator.getNonEmptyString(scanner, "\n🔍 Enter product name, category, or first letter: ");
    }

    public String getProductId() {
        return InputValidator.getNonEmptyString(scanner, "Enter product code (e.g. 001): ");
    }

    public int getQuantity() {
        return InputValidator.getValidInt(scanner, "Enter quantity: ", 1, 999);
    }

    public boolean confirmCheckout(double total) {
        System.out.printf("\n💰 Total Amount: $%.2f\n", total);
        String response = InputValidator.getNonEmptyString(scanner, "Confirm checkout? (y/n): ").toLowerCase();
        return response.equals("y") || response.equals("yes");
    }

    public void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}