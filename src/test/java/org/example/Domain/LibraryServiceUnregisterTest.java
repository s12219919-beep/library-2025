package org.example.Domain;



import org.junit.jupiter.api.Test;
import service.LibraryService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceUnregisterTest {

    @Test
    void userWithActiveLoanCannotBeUnregistered() throws Exception {
        LibraryService service = new LibraryService();
        service.adminLogin("admin", "1234");
        service.registerUser("u1", "User 1");

        User user = getUserByReflection(service, "u1");

        Loan activeLoan = new Loan("b1", "u1", "BOOK",
                new BookFineStrategy(),
                LocalDate.now().minusDays(5));
        user.addLoan(activeLoan);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.unregisterUser("u1"));
        assertTrue(ex.getMessage().toLowerCase().contains("active loans"));
    }

    @Test
    void userWithoutLoansAndFinesCanBeUnregistered() throws Exception {
        LibraryService service = new LibraryService();
        service.adminLogin("admin", "1234");
        service.registerUser("u1", "User 1");

        assertDoesNotThrow(() -> service.unregisterUser("u1"));
    }

    private User getUserByReflection(LibraryService service, String id) throws Exception {
        java.lang.reflect.Field usersField =
                LibraryService.class.getDeclaredField("users");
        usersField.setAccessible(true);
        java.util.List<User> users =
                (java.util.List<User>) usersField.get(service);
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
