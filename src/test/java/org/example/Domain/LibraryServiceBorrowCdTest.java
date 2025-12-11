package org.example.Domain;



import org.example.Domain.*;
import org.junit.jupiter.api.Test;
import service.LibraryService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceBorrowCdTest {

    @Test
    void userCanBorrowCdWhenNoFinesOrOverdue() {
        LibraryService service = new LibraryService();
        service.adminLogin("admin", "1234");
        service.addCD("c1", "Title", "Artist");
        service.registerUser("u1", "User 1");

        assertDoesNotThrow(() -> service.borrowCD("u1", "c1"));
    }

    @Test
    void payFineReducesUserBalance() throws Exception {
        LibraryService service = new LibraryService();
        service.registerUser("u1", "User 1");

        User user = getUserByReflection(service, "u1");
        user.addFine(50.0);

        service.payFine("u1", 20.0);

        assertEquals(30.0, user.getFineBalance());
    }

    @Test
    void sendOverdueRemindersWithNoOverdueDoesNothing() {
        LibraryService service = new LibraryService();
        service.sendOverdueReminders();
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
