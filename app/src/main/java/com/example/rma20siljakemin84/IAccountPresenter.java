package com.example.rma20siljakemin84;

public interface IAccountPresenter{
    void updateBudget(double amount) throws IllegalAmountException;
    void updateAccount(double budget, double totalLimit, double monthLimit, boolean connected);
    double getBudget();
    double getOverallLimit();
    double getMonthlyLimit();
}
