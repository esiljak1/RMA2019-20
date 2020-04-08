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
    public double getBudget() {
        return interactor.get().getBudget();
    }
    public void updateBudget(double iznos){
        interactor.get().setBudget(interactor.get().getBudget() - iznos);
    }
    public void setBudget(double budget){
        interactor.get().setBudget(budget);
    }
    public void setOverallLimit(double limit){
        interactor.get().setTotalLimit(limit);
    }
    public void setMonthlyLimit(double limit){
        interactor.get().setMonthLimit(limit);
    }
}
