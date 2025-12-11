package org.example.Domain;

public class EmailObserver implements Observer {
    private service.RealEmailService emailService;

    public EmailObserver(service.RealEmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void update(String userId, String message) {
        emailService.sendEmail(userId + "@example.com", message);
    }
}
