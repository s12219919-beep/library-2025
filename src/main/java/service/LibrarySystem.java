package service;

import org.example.Domain.Book;
import org.example.Domain.Loan;
import java.util.List;
import java.util.Scanner;

public class LibrarySystem {
    private static LibraryService library = new LibraryService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Library Management System - Sprint 5");
        showMenu();
    }

    private static void showMenu() {
        while (true) {
            System.out.println("\n1. Admin Login");
            System.out.println("2. Add Book");
            System.out.println("3. Add CD");
            System.out.println("4. Search Book");
            System.out.println("5. Register User");
            System.out.println("6. Borrow Book");
            System.out.println("7. Borrow CD");
            System.out.println("8. Show Overdue Items");
            System.out.println("9. Pay Fine");
            System.out.println("10. Show User Total Fine");
            System.out.println("11. Send Overdue Reminders");
            System.out.println("12. Unregister User");
            System.out.println("13. Admin Logout");
            System.out.println("14. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: adminLogin(); break;
                case 2: addBook(); break;
                case 3: addCD(); break;
                case 4: searchBook(); break;
                case 5: registerUser(); break;
                case 6: borrowBook(); break;
                case 7: borrowCD(); break;
                case 8: showOverdue(); break;
                case 9: payFine(); break;
                case 10: showUserTotalFine(); break;
                case 11: sendReminders(); break;
                case 12: unregisterUser(); break;
                case 13: library.adminLogout(); break;
                case 14: return;
            }
        }
    }

    private static void adminLogin() {
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        if (library.adminLogin(user, pass)) {
            System.out.println("Login successful");
        } else {
            System.out.println("Invalid credentials");
        }
    }

    private static void addBook() {
        try {
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine();
            System.out.print("Title: ");
            String title = scanner.nextLine();
            System.out.print("Author: ");
            String author = scanner.nextLine();
            library.addBook(isbn, title, author);
            System.out.println("Book added successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addCD() {
        try {
            System.out.print("CD ID: ");
            String id = scanner.nextLine();
            System.out.print("Title: ");
            String title = scanner.nextLine();
            System.out.print("Artist: ");
            String artist = scanner.nextLine();
            library.addCD(id, title, artist);
            System.out.println("CD added successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void searchBook() {
        System.out.print("Search: ");
        String query = scanner.nextLine();
        List<Book> results = library.searchBooks(query);
        if (results.isEmpty()) {
            System.out.println("No results found");
        } else {
            results.forEach(b ->
                    System.out.println("- " + b.getTitle() + " - " + b.getAuthor()));
        }
    }

    private static void registerUser() {
        System.out.print("User ID: ");
        String id = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        library.registerUser(id, name);
        System.out.println("User registered");
    }

    private static void borrowBook() {
        try {
            System.out.print("User ID: ");
            String userId = scanner.nextLine();
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine();
            library.borrowBook(userId, isbn);
            System.out.println("Book borrowed successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void borrowCD() {
        try {
            System.out.print("User ID: ");
            String userId = scanner.nextLine();
            System.out.print("CD ID: ");
            String cdId = scanner.nextLine();
            library.borrowCD(userId, cdId);
            System.out.println("CD borrowed successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showOverdue() {
        List<Loan> overdue = library.getOverdueLoans();
        if (overdue.isEmpty()) {
            System.out.println("No overdue items");
        } else {
            overdue.forEach(l ->
                    System.out.println("- Media: " + l.getMediaId() +
                            " | Type: " + l.getMediaType() +
                            " | Due: " + l.getDueDate() +
                            " | Fine: " + l.calculateFine()));
        }
    }

    private static void payFine() {
        System.out.print("User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        library.payFine(userId, amount);
        System.out.println("Fine paid");
    }

    private static void showUserTotalFine() {
        System.out.print("User ID: ");
        String userId = scanner.nextLine();
        double total = library.calculateUserTotalFine(userId);
        System.out.println("Total fine for user " + userId + " = " + total);
    }

    private static void sendReminders() {
        try {
            library.sendOverdueReminders();
            System.out.println("Reminders sent");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void unregisterUser() {
        System.out.print("User ID: ");
        String userId = scanner.nextLine();
        try {
            library.unregisterUser(userId);
            System.out.println("User unregistered");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
