package com.example.rma20siljakemin84;

public interface IAccountInteractor {
    void getAccountDetails();
    void updateAccount(String budget, String totalLimit, String monthLimit);
    void setPresenter(AccountPresenter presenter);
    AccountPresenter getPresenter();
}
