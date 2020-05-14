package com.example.rma20siljakemin84;

public class AccountModel implements IAccountModel {
    private int id = 0;
    private double budget = 0;
    private double totalLimit = 0;
    private double monthLimit = 0;

    private static AccountModel instance = new AccountModel();

    private AccountModel() {
    }

    public AccountModel(int id, double budget, double totalLimit, double monthLimit) {
        this.id = id;
        setBudget(budget);
        setTotalLimit(totalLimit);
        setMonthLimit(monthLimit);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
