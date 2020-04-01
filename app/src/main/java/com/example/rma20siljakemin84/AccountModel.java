package com.example.rma20siljakemin84;

public class AccountModel implements IAccountModel {
    private double budget = 100000;
    private double totalLimit = 100000;
    private double monthLimit = 10000;

    private static AccountModel instance = new AccountModel();

    private AccountModel() {
    }

    private void notNegativeNumberTest(double number){
        if(number < 0) throw new IllegalArgumentException("Can't be negative");
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        notNegativeNumberTest(budget);
        this.budget = budget;
    }

    public double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(double totalLimit) {
        notNegativeNumberTest(totalLimit);
        this.totalLimit = totalLimit;
    }

    public double getMonthLimit() {
        return monthLimit;
    }

    public void setMonthLimit(double monthLimit) {
        notNegativeNumberTest(monthLimit);
        this.monthLimit = monthLimit;
    }

    public static AccountModel getInstance(){
        return instance;
    }
}
