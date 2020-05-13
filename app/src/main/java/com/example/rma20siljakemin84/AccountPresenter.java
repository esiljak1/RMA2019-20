package com.example.rma20siljakemin84;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountPresenter implements IAccountPresenter, Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
