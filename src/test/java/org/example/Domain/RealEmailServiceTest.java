package org.example.Domain;



import org.junit.jupiter.api.Test;
import service.RealEmailService;

import static org.junit.jupiter.api.Assertions.*;

public class RealEmailServiceTest {

    @Test
    void realEmailServiceCanBeConstructed() {
        RealEmailService service = new RealEmailService();
        assertNotNull(service);
    }
}

