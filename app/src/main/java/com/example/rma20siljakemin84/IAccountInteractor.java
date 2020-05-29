package com.example.rma20siljakemin84;

public interface IAccountInteractor {
    void getAccountDetails(boolean connectedToInternet);
    void updateAccount(String budget, String totalLimit, String monthLimit, boolean connectedToInternet);
    void setPresenter(AccountPresenter presenter);
    AccountPresenter getPresenter();
}
