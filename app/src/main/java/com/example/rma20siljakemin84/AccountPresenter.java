package com.example.rma20siljakemin84;

public class AccountPresenter implements IAccountPresenter {

    private MainActivity view;
    private AccountInteractor interactor;

    public AccountPresenter(MainActivity view) {
        this.view = view;
        interactor = new AccountInteractor();
    }
    public double getMonthlyLimit(){
        return interactor.get().getMonthLimit();
    }
    public double getOverallLimit(){
        return interactor.get().getTotalLimit();
    }
}
