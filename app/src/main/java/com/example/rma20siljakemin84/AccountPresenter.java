package com.example.rma20siljakemin84;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountPresenter implements IAccountPresenter, Parcelable, AccountInteractor.OnAccountSearchDone {

    private IAccountView view;
    private AccountInteractor interactor;

    public AccountPresenter(IAccountView view) {
        this.view = view;
        interactor = new AccountInteractor();
    }

    public AccountPresenter() {
        interactor = new AccountInteractor();
    }

    protected AccountPresenter(Parcel in) {
    }

    public static final Creator<AccountPresenter> CREATOR = new Creator<AccountPresenter>() {
        @Override
        public AccountPresenter createFromParcel(Parcel in) {
            return new AccountPresenter(in);
        }

        @Override
        public AccountPresenter[] newArray(int size) {
            return new AccountPresenter[size];
        }
    };

    public double getMonthlyLimit(){
        return interactor.getAccount().getMonthLimit();
    }
    public double getOverallLimit(){
        return interactor.getAccount().getTotalLimit();
    }
    public double getBudget() {
        return interactor.getAccount().getBudget();
    }
    public void updateBudget(double iznos) throws IllegalAmountException {
        interactor.getAccount().setBudget(interactor.getAccount().getBudget() - iznos);
    }
    public void setBudget(double budget) throws IllegalAmountException {
        interactor.getAccount().setBudget(budget);
    }
    public void setOverallLimit(double limit) throws IllegalAmountException {
        interactor.getAccount().setTotalLimit(limit);
    }
    public void setMonthlyLimit(double limit) throws IllegalAmountException {
        interactor.getAccount().setMonthLimit(limit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public void onDone(AccountModel account) {
        ((AccountInteractor) interactor).setAccount(account);
        view.notifyAccountDetailsChanged();
    }

    public void getDetailsFromWeb(){
        new AccountInteractor((AccountInteractor.OnAccountSearchDone)this).execute("");
    }
}
