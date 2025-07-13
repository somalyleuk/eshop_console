package com.eshope_console.controller;

import com.eshope_console.service.ReadProductService;
import com.eshope_console.service.ProductService;
import com.eshope_console.util.ConsoleColors;
import com.eshope_console.view.ConsoleView;
import java.util.Scanner;

public class ReadProductController {
    private final Scanner scanner;
    private final ConsoleView consoleView;
    private final ReadProductService readProductService;
    private final ProductService productService;

    public ReadProductController(Scanner scanner, ConsoleView consoleView,
                                 ReadProductService readProductService,
                                 ProductService productService) {
        this.scanner = scanner;
        this.consoleView = consoleView;
        this.readProductService = readProductService;
        this.productService = productService;
    }

    public void showReadProductMenu() {
        while (true) {
            System.out.println(ConsoleColors.CYAN_BOLD + "\n📊 PRODUCT READ OPERATIONS MENU" + ConsoleColors.RESET);
            System.out.println("1. Insert 10 Million Products (Prompt Truncate)");
            System.out.println("2. Read 10 Million Products");
            System.out.println("3. Back to Main Menu");
            System.out.print("\nEnter your choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> handleInsert10MillionProductsWithTruncatePrompt();
                case 2 -> handleRead10MillionProducts();
                case 3 -> {
                    readProductService.shutdown();
                    return;
                }
                default -> consoleView.showError("Invalid choice. Please try again.");
            }
        }
    }

    private void handleInsert10MillionProductsWithTruncatePrompt() {
        System.out.println(ConsoleColors.YELLOW_BOLD + "\n⚠️  This will insert 10,000,000 products!" + ConsoleColors.RESET);
        System.out.println("Do you want to clear the products table before inserting? (yes/no): ");
        String truncate = scanner.nextLine().trim().toLowerCase();
        boolean shouldTruncate = "yes".equals(truncate) || "y".equals(truncate);
        readProductService.insert10MillionProducts(shouldTruncate);
    }

    private void handleRead10MillionProducts() {
        readProductService.read10MillionProducts();
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
} 