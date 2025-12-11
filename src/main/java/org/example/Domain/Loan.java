package org.example.Domain;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private String mediaId;
    private String userId;
    private String mediaType;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned = false;
    private FineStrategy fineStrategy;


    public Loan(String mediaId, String userId, String mediaType, FineStrategy fineStrategy) {
        this(mediaId, userId, mediaType, fineStrategy, LocalDate.now());
    }


    public Loan(String mediaId, String userId, String mediaType,
                FineStrategy fineStrategy, LocalDate borrowDate) {
        this.mediaId = mediaId;
        this.userId = userId;
        this.mediaType = mediaType;
        this.fineStrategy = fineStrategy;
        this.borrowDate = borrowDate;
        this.dueDate = mediaType.equals("CD")
                ? borrowDate.plusDays(7)
                : borrowDate.plusDays(28);
    }

    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }

    public long overdueDays() {
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public double calculateFine() {
        if (!isOverdue()) return 0.0;
        return fineStrategy.calculateFine(overdueDays());
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
