package org.example.Domain;



import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    void bookGettersAndAvailabilityAndSearchWork() {
        Book book = new Book("b1", "Java Basics", "Author Name");

        assertEquals("b1", book.getId());
        assertEquals("Java Basics", book.getTitle());
        assertEquals("Author Name", book.getAuthor());

        assertTrue(book.isAvailable());
        book.setAvailable(false);
        assertFalse(book.isAvailable());

        assertTrue(book.matchesSearch("Java"));
        assertTrue(book.matchesSearch("Author"));
        assertTrue(book.matchesSearch("b1"));
        assertFalse(book.matchesSearch("NotFound"));
    }
}
