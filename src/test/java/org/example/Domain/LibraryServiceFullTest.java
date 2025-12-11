package org.example.Domain;


import org.example.Domain.*;
import org.junit.jupiter.api.Test;
import service.LibraryService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceFullTest {

    @Test
    void addBookAndSearchWorks() {
        LibraryService s = new LibraryService();
        s.adminLogin("admin", "1234");
        s.addBook("b1", "Java", "Author");
        List<Book> res = s.searchBooks("Java");
        assertFalse(res.isEmpty());
    }

    @Test
    void addCdAndBorrowCdSuccess() {
        LibraryService s = new LibraryService();
        s.adminLogin("admin", "1234");
        s.addCD("c1", "Hits", "Artist");
        s.registerUser("u1", "User 1");
        assertDoesNotThrow(() -> s.borrowCD("u1", "c1"));
    }

    @Test
    void borrowBookSuccess() {
        LibraryService s = new LibraryService();
        s.adminLogin("admin", "1234");
        s.addBook("b1", "Java", "Author");
        s.registerUser("u1", "User 1");
        assertDoesNotThrow(() -> s.borrowBook("u1", "b1"));
    }

    @Test
    void borrowCdBlockedByUnpaidFine() throws Exception {
        LibraryService s = new LibraryService();
        s.adminLogin("admin", "1234");
        s.addCD("c1", "Hits", "Artist");
        s.registerUser("u1", "User 1");

        User u = getUserByReflection(s, "u1");
        u.addFine(10.0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> s.borrowCD("u1", "c1"));
        assertTrue(ex.getMessage().toLowerCase().contains("unpaid"));
    }

    @Test
    void borrowBookBlockedByOverdueLoan() throws Exception {
        LibraryService s = new LibraryService();
        s.adminLogin("admin", "1234");
        s.addBook("b1", "Java", "Author");
        s.addBook("b2", "More Java", "Author");
        s.registerUser("u1", "User 1");

        User u = getUserByReflection(s, "u1");
        Loan overdue = new Loan("b1", "u1", "BOOK",
                new BookFineStrategy(),
                LocalDate.now().minusDays(40));
        u.addLoan(overdue);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> s.borrowBook("u1", "b2"));
        assertTrue(ex.getMessage().toLowerCase().contains("overdue"));
    }

    @Test
    void unregisterUserBlockedByActiveLoan() throws Exception {
        LibraryService s = new LibraryService();
        s.adminLogin("admin", "1234");
        s.registerUser("u1", "User 1");

        User u = getUserByReflection(s, "u1");
        Loan loan = new Loan("b1", "u1", "BOOK",
                new BookFineStrategy(),
                LocalDate.now().minusDays(5));
        u.addLoan(loan);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> s.unregisterUser("u1"));
        assertTrue(ex.getMessage().toLowerCase().contains("active loans"));
    }

    @Test
    void unregisterUserSuccessWhenNoLoansOrFines() {
        LibraryService s = new LibraryService();
        s.adminLogin("admin", "1234");
        s.registerUser("u1", "User 1");
        assertDoesNotThrow(() -> s.unregisterUser("u1"));
    }

    @Test
    void calculateUserTotalFineMixBookAndCd() throws Exception {
        LibraryService s = new LibraryService();
        s.registerUser("u1", "User 1");

        User u = getUserByReflection(s, "u1");
        Loan bookLoan = new Loan("b1", "u1", "BOOK",
                new BookFineStrategy(),
                LocalDate.now().minusDays(31)); // 3 days overdue
        Loan cdLoan = new Loan("c1", "u1", "CD",
                new CDFineStrategy(),
                LocalDate.now().minusDays(10)); // 3 days overdue
        u.addLoan(bookLoan);
        u.addLoan(cdLoan);

        double total = s.calculateUserTotalFine("u1");
        assertEquals(30.0 + 60.0, total);
    }
    @Test
    void getOverdueLoansReturnsCorrectList() throws Exception {
        LibraryService s = new LibraryService();
        s.registerUser("u1", "User 1");

        User u = getUserByReflection(s, "u1");

        Loan overdue = new Loan("b1", "u1", "BOOK",
                new BookFineStrategy(),
                LocalDate.now().minusDays(40));
        Loan notOverdue = new Loan("b2", "u1", "BOOK",
                new BookFineStrategy(),
                LocalDate.now());

        u.addLoan(overdue);
        u.addLoan(notOverdue);

        java.lang.reflect.Field loansField =
                LibraryService.class.getDeclaredField("loans");
        loansField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<Loan> loans =
                (java.util.List<Loan>) loansField.get(s);
        loans.add(overdue);
        loans.add(notOverdue);

        java.util.List<Loan> list = s.getOverdueLoans();
        assertEquals(1, list.size());
        assertEquals("b1", list.get(0).getMediaId());
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
