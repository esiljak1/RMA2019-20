package com.example.rma20siljakemin84;

import android.content.Context;

public interface IAccountPresenter{
    void updateBudget(double amount) throws IllegalAmountException;
    void updateAccount(double budget, double totalLimit, double monthLimit, boolean connected, Context context);
    double getBudget();
    double getOverallLimit();
    double getMonthlyLimit();
    void updateOnlineAccount(Context context, boolean connectedToInternet);
}
