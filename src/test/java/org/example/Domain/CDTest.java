package org.example.Domain;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CDTest {

    @Test
    void cdMatchesSearchByIdTitleAndArtist() {
        CD cd = new CD("cd1", "Best Hits", "Artist Name");

        assertTrue(cd.matchesSearch("cd1"));
        assertTrue(cd.matchesSearch("Best"));
        assertTrue(cd.matchesSearch("Artist"));
    }
}
