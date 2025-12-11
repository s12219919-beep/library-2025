package org.example.Domain;




import org.junit.jupiter.api.Test;
import service.LibraryService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceFineTest {

    @Test
    void calculateUserTotalFineCountsBooksAndCds() throws Exception {
        LibraryService service = new LibraryService();
        service.adminLogin("admin", "1234");
        service.registerUser("u1", "User 1");

        User user = getUserByReflection(service, "u1");

        Loan bookLoan = new Loan("b1", "u1", "BOOK",
                new BookFineStrategy(),
                LocalDate.now().minusDays(31));
        Loan cdLoan = new Loan("c1", "u1", "CD",
                new CDFineStrategy(),
                LocalDate.now().minusDays(10));

        user.addLoan(bookLoan);
        user.addLoan(cdLoan);

        double total = service.calculateUserTotalFine("u1");

        assertEquals(30.0 + 60.0, total);
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
