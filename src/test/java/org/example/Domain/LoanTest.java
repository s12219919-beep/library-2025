package org.example.Domain;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class LoanTest {
    @Test
    void loanNotOverdueHasZeroFine() {
        FineStrategy strategy = new BookFineStrategy();
        LocalDate borrowDate = LocalDate.now();
        Loan loan = new Loan("b2", "u2", "BOOK", strategy, borrowDate);

        assertFalse(loan.isOverdue());
        assertEquals(0.0, loan.calculateFine());

        assertEquals("b2", loan.getMediaId());
        assertEquals("u2", loan.getUserId());
        assertEquals("BOOK", loan.getMediaType());
        assertFalse(loan.isReturned());
        loan.setReturned(true);
        assertTrue(loan.isReturned());
    }


    @Test
    void bookLoanBecomesOverdueAfter28Days() {
        FineStrategy strategy = new BookFineStrategy();
        LocalDate borrowDate = LocalDate.now().minusDays(30);
        Loan loan = new Loan("b1", "u1", "BOOK", strategy, borrowDate);

        assertTrue(loan.isOverdue());
        assertTrue(loan.calculateFine() > 0);
    }

    @Test
    void cdLoanUsesHigherFine() {
        FineStrategy strategy = new CDFineStrategy();
        LocalDate borrowDate = LocalDate.now().minusDays(10);
        Loan loan = new Loan("c1", "u1", "CD", strategy, borrowDate);

        assertTrue(loan.isOverdue());

        assertEquals(60.0, loan.calculateFine());
    }
}
