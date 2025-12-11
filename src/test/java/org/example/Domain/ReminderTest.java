package org.example.Domain;


import org.example.Domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.LibraryService;
import service.RealEmailService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


import org.example.Domain.EmailObserver;
import org.example.Domain.Observer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class ReminderTest {

    @Test
    void emailObserverShouldCallRealEmailService() {
        RealEmailService emailMock = Mockito.mock(RealEmailService.class);

        Observer observer = new EmailObserver(emailMock);

        observer.update("u1", "You have 1 overdue book");

        verify(emailMock, times(1))
                .sendEmail(anyString(), anyString());
    }
}
