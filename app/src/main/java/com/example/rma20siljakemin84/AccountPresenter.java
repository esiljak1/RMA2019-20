package com.example.rma20siljakemin84;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class AccountPresenter implements IAccountPresenter, Parcelable {

    private IAccountView view;
    private AccountInteractor interactor;
    private Context context;


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

    public void fetchedAccountDetails(AccountModel account) {
        interactor.setAccount(account);
        view.notifyAccountDetailsChanged();
    }

    public void getDetailsForAccount(boolean connectedToTheInternet, Context context){
        this.context = context;
        interactor.setPresenter(this);
        interactor.getAccountDetails(connectedToTheInternet);
    }

    public void updateAccount(double budget, double totalLimit, double monthLimit, boolean connectedToInternet, Context context){
        this.context = context;
        interactor.setPresenter(this);
        interactor.updateAccount(budget + "", totalLimit + "", monthLimit + "", connectedToInternet);
    }

    public IAccountView getView() {
        return view;
    }

    public void setView(IAccountView view) {
        this.view = view;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void updateOnlineAccount(Context context, boolean connectedToInternet) {
        if(!connectedToInternet) return;
        interactor.setPresenter(this);
        interactor.updateFromDatabase(context);
    }
}
