package com.eshope_console;

import java.util.Scanner;

import com.eshope_console.config.DatabaseConfig;
import com.eshope_console.dao.*;
import com.eshope_console.service.*;
import com.eshope_console.view.*;
import com.eshope_console.controller.ShopController;
import com.eshope_console.controller.ReadProductController;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            DatabaseConfig.testConnection();

            UserDAO userDAO = new UserDAO();
            ProductDAO productDAO = new ProductDAO();
            OrderDAO orderDAO = new OrderDAO();

            AuthService authService = new AuthService(userDAO);
            ProductService productService = new ProductService(productDAO);
            CartService cartService = new CartService(productService);
            OrderService orderService = new OrderService(orderDAO, productService);
            ReadProductService readProductService = new ReadProductService(productService);

            Scanner scanner = new Scanner(System.in);
            MenuView menuView = new MenuView(scanner);
            ConsoleView consoleView = new ConsoleView();

            ReadProductController readProductController = new ReadProductController(
                    scanner, consoleView, readProductService, productService
            );

            ShopController controller = new ShopController(
                    scanner, menuView, consoleView,
                    authService, productService, cartService, orderService, readProductController
            );

            controller.start();

        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}