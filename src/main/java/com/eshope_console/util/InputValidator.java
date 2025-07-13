package com.eshope_console.util;

import java.util.Scanner;
import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isNonEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static int getValidInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Please enter a number between %d and %d.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println(" Please enter a valid number.");
            }
        }
    }

    public static String getNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (isNonEmpty(input)) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }
}