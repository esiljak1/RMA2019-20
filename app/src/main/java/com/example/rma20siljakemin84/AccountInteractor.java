package com.example.rma20siljakemin84;

public class AccountInteractor implements IAccountInteractor {
    private AccountModel model = AccountModel.getInstance();

    public AccountModel get(){
        return model;
    }
}
