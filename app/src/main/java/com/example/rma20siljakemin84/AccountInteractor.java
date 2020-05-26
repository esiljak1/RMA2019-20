package com.example.rma20siljakemin84;

public class AccountInteractor implements IAccountInteractor, GETAccountDetails.OnAccountSearchDone, POSTUpdateAccount.OnAccountUpdateDone {
    private AccountModel model = AccountModel.getInstance();
    private AccountPresenter presenter;

    public AccountModel getAccount(){
        return model;
    }

    public AccountInteractor() {
    }

    public void setAccount(AccountModel model) {
        this.model = model;
    }

    @Override
    public AccountPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(AccountPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getAccountDetails() {
        new GETAccountDetails(this).execute();
    }

    @Override
    public void updateAccount(String budget, String totalLimit, String monthLimit) {
        new POSTUpdateAccount(this).execute(budget, totalLimit, monthLimit);
    }

    @Override
    public void onSearchDone(AccountModel account) {
        presenter.fetchedAccountDetails(account);
    }

    @Override
    public void onUpdateDone(AccountModel account) {
        presenter.fetchedAccountDetails(account);
    }
}
