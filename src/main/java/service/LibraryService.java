package service;

import org.example.Domain.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class LibraryService {
    private static final String USERS_FILE = "users.txt";
    private static final String BOOKS_FILE = "books.txt";
    private static final String CDS_FILE   = "cds.txt";

    private Admin currentAdmin;
    private List<MediaItem> mediaItems = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();
    private RealEmailService emailService = new RealEmailService();
    private List<Observer> observers = new ArrayList<>();

    public LibraryService() {
        observers.add(new EmailObserver(emailService));
        loadUsersFromFile();
        loadBooksFromFile();
        loadCdsFromFile();
    }

    public boolean adminLogin(String username, String password) {
        currentAdmin = new Admin("admin", "1234");
        return currentAdmin.login(username, password);
    }

    public void adminLogout() {
        if (currentAdmin != null) currentAdmin.logout();
    }

    public boolean isAdminLoggedIn() {
        return currentAdmin != null && currentAdmin.isLoggedIn();
    }


    public void addBook(String isbn, String title, String author) {
        if (!isAdminLoggedIn()) throw new RuntimeException("Admin login required");
        mediaItems.add(new Book(isbn, title, author));
        saveBooksToFile();
    }

    public List<Book> searchBooks(String query) {
        List<Book> results = new ArrayList<>();
        for (MediaItem item : mediaItems) {
            if (item instanceof Book && ((Book) item).matchesSearch(query)) {
                results.add((Book) item);
            }
        }
        return results;
    }


    public void addCD(String id, String title, String artist) {
        if (!isAdminLoggedIn()) throw new RuntimeException("Admin login required");
        mediaItems.add(new CD(id, title, artist));
        saveCdsToFile();
    }

    public void registerUser(String id, String name) {
        users.add(new User(id, name));
        saveUsersToFile();
    }

    private void checkBorrowRestrictions(User user) {
        if (user.getFineBalance() > 0) {
            throw new RuntimeException("Cannot borrow: unpaid fines exist");
        }
        if (user.hasOverdueLoans()) {
            throw new RuntimeException("Cannot borrow: user has overdue loans");
        }
    }

    public void borrowBook(String userId, String isbn) {
        User user = findUser(userId);
        MediaItem book = findAvailableMedia(isbn, "BOOK");
        checkBorrowRestrictions(user);

        Loan loan = new Loan(isbn, userId, "BOOK", new BookFineStrategy());
        loans.add(loan);
        user.addLoan(loan);
        book.setAvailable(false);
    }

    public void borrowCD(String userId, String cdId) {
        User user = findUser(userId);
        MediaItem cd = findAvailableMedia(cdId, "CD");
        checkBorrowRestrictions(user);

        Loan loan = new Loan(cdId, userId, "CD", new CDFineStrategy());
        loans.add(loan);
        user.addLoan(loan);
        cd.setAvailable(false);
    }

    public void unregisterUser(String userId) {
        User user = findUser(userId);

        if (user.hasActiveLoans()) {
            throw new RuntimeException("Cannot unregister: user has active loans");
        }
        if (user.getFineBalance() > 0) {
            throw new RuntimeException("Cannot unregister: user has unpaid fines");
        }

        users.remove(user);
        saveUsersToFile();
    }

    public List<Loan> getOverdueLoans() {
        return loans.stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
    }

    public void payFine(String userId, double amount) {
        User user = findUser(userId);
        user.payFine(amount);
    }

    public double calculateUserTotalFine(String userId) {
        User user = findUser(userId);
        double total = 0.0;
        for (Loan loan : user.getLoans()) {
            total += loan.calculateFine();
        }
        return total;
    }

    public void sendOverdueReminders() {
        List<Loan> overdueLoans = getOverdueLoans();

        for (Loan loan : overdueLoans) {
            String userId = loan.getUserId();
            long overdueCount = getOverdueLoans().stream()
                    .filter(l -> l.getUserId().equals(userId))
                    .count();

            String message = "You have " + overdueCount + " overdue items.";
            notifyObservers(userId, message);
        }
    }

    public void notifyObservers(String userId, String message) {
        for (Observer observer : observers) {
            observer.update(userId, message);
        }
    }

    private User findUser(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private MediaItem findAvailableMedia(String id, String type) {
        return mediaItems.stream()
                .filter(m -> m.getId().equals(id)
                        && m.isAvailable()
                        && ((type.equals("BOOK") && m instanceof Book)
                        || (type.equals("CD") && m instanceof CD)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Media not available"));
    }



    private void loadUsersFromFile() {
        Path path = Paths.get(USERS_FILE);
        if (!Files.exists(path)) return;

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    users.add(new User(parts[0], parts[1]));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }

    private void saveUsersToFile() {
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(u.getId() + ";" + u.getName());
        }
        try {
            Files.write(Paths.get(USERS_FILE), lines, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }

    private void loadBooksFromFile() {
        Path path = Paths.get(BOOKS_FILE);
        if (!Files.exists(path)) return;

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", 3);
                if (parts.length == 3) {
                    mediaItems.add(new Book(parts[0], parts[1], parts[2]));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load books: " + e.getMessage());
        }
    }

    private void saveBooksToFile() {
        List<String> lines = new ArrayList<>();
        for (MediaItem item : mediaItems) {
            if (item instanceof Book) {
                Book b = (Book) item;
                lines.add(b.getId() + ";" + b.getTitle() + ";" + b.getAuthor());
            }
        }
        try {
            Files.write(Paths.get(BOOKS_FILE), lines, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Failed to save books: " + e.getMessage());
        }
    }

    private void loadCdsFromFile() {
        Path path = Paths.get(CDS_FILE);
        if (!Files.exists(path)) return;

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", 3);
                if (parts.length == 3) {
                    mediaItems.add(new CD(parts[0], parts[1], parts[2]));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load CDs: " + e.getMessage());
        }
    }

    private void saveCdsToFile() {
        List<String> lines = new ArrayList<>();
        for (MediaItem item : mediaItems) {
            if (item instanceof CD) {
                CD cd = (CD) item;
                lines.add(cd.getId() + ";" + cd.getTitle() + ";" + cd.getAuthor());
            }
        }
        try {
            Files.write(Paths.get(CDS_FILE), lines, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Failed to save CDs: " + e.getMessage());
        }
    }
}
