package org.example.Domain;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FineStrategyTest {

    @Test
    void bookFineShouldBe10PerDay() {
        FineStrategy bookStrategy = new BookFineStrategy();
        double fine = bookStrategy.calculateFine(3); // 3 days
        assertEquals(30.0, fine);
    }

    @Test
    void cdFineShouldBe20PerDay() {
        FineStrategy cdStrategy = new CDFineStrategy();
        double fine = cdStrategy.calculateFine(3); // 3 days
        assertEquals(60.0, fine);
    }
}
