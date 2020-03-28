package com.example.rma20siljakemin84;

public class AccountModel implements IAccountModel {
    private double budget = 1000;
    private double totalLimit = 100000;
    private double monthLimit = 10000;

    private static AccountModel instance = new AccountModel();

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(double totalLimit) {
        this.totalLimit = totalLimit;
    }

    public double getMonthLimit() {
        return monthLimit;
    }

    public void setMonthLimit(double monthLimit) {
        this.monthLimit = monthLimit;
    }

    public static AccountModel getInstance(){
        return instance;
    }
}
