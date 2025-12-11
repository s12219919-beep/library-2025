package org.example.Domain;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String name;
    private List<Loan> loans = new ArrayList<>();
    private double fineBalance = 0.0;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Loan> getLoans() { return loans; }
    public double getFineBalance() { return fineBalance; }

    public void addLoan(Loan loan) { loans.add(loan); }
    public void addFine(double amount) { fineBalance += amount; }
    public void payFine(double amount) { fineBalance = Math.max(0, fineBalance - amount); }
    public boolean hasActiveLoans() {
        return loans.stream().anyMatch(l -> !l.isReturned());
    }

    public boolean hasOverdueLoans() {
        return loans.stream().anyMatch(Loan::isOverdue);
    }

}
