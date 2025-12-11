package org.example.Domain;


import org.example.Domain.*;import org.junit.jupiter.api.Test;
import service.LibraryService;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceBorrowTest {

    @Test
    void userWithUnpaidFineCannotBorrowBook() {
        LibraryService service = new LibraryService();
        service.adminLogin("admin", "1234");
        service.addBook("b1", "Test Book", "Author");
        service.registerUser("u1", "User 1");


        User user = getUserByReflection(service, "u1");
        user.addFine(20.0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.borrowBook("u1", "b1"));
        assertTrue(ex.getMessage().toLowerCase().contains("unpaid"));
    }

    @Test
    void userWithOverdueLoanCannotBorrowBook() {
        LibraryService service = new LibraryService();
        service.adminLogin("admin", "1234");
        service.addBook("b1", "Test Book", "Author");
        service.addBook("b2", "Another Book", "Author");
        service.registerUser("u1", "User 1");

        User user = getUserByReflection(service, "u1");


        Loan oldLoan = new Loan("b1", "u1", "BOOK",
                new BookFineStrategy(),
                java.time.LocalDate.now().minusDays(40));
        user.addLoan(oldLoan);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.borrowBook("u1", "b2"));
        assertTrue(ex.getMessage().toLowerCase().contains("overdue"));
    }


    private User getUserByReflection(LibraryService service, String id) {
        try {
            java.lang.reflect.Field usersField =
                    LibraryService.class.getDeclaredField("users");
            usersField.setAccessible(true);
            java.util.List<User> users =
                    (java.util.List<User>) usersField.get(service);
            return users.stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
