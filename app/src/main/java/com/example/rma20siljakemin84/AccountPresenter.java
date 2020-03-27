package com.example.rma20siljakemin84;

public class AccountPresenter implements IAccountPresenter {

    private MainActivity view;
    private AccountModel model;

    public AccountPresenter(MainActivity view) {
        this.view = view;
        model = new AccountModel();
    }
    public double getMonthlyLimit(){
        return model.getMonthLimit();
    }
    public double getOverallLimit(){
        return model.getTotalLimit();
    }
}
