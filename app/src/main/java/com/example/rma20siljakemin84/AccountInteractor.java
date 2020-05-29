package com.example.rma20siljakemin84;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccountInteractor implements IAccountInteractor, GETAccountDetails.OnAccountSearchDone, POSTUpdateAccount.OnAccountUpdateDone {
    private AccountModel model = AccountModel.getInstance();
    private AccountPresenter presenter;
    private TransactionDBOpenHelper transactionDBOpenHelper;
    private SQLiteDatabase database;

    private boolean isDatabaseEmpty(SQLiteDatabase database){
        String query = "SELECT * FROM " + TransactionDBOpenHelper.ACCOUNT_TABLE;
        Cursor cursor = database.rawQuery(query, null);
        return cursor.getCount() == 0;
    }

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
    public void getAccountDetails(boolean connectedToInternet) {
        if(connectedToInternet){
            new GETAccountDetails(this).execute();
        }else{
            transactionDBOpenHelper = new TransactionDBOpenHelper(presenter.getContext());
            database = transactionDBOpenHelper.getWritableDatabase();
            String query = "SELECT *" + " FROM "
                    + TransactionDBOpenHelper.ACCOUNT_TABLE + " ORDER BY " + TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID;
            Cursor cursor = database.rawQuery(query, null);
            if(cursor.moveToFirst()){
                int idPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_ID);
                int amountPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_AMOUNT);
                int totalLimitPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT);
                int monthLimitPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT);
                try {
                    presenter.fetchedAccountDetails(new AccountModel(cursor.getInt(idPos), cursor.getDouble(amountPos),
                            cursor.getDouble(totalLimitPos), cursor.getDouble(monthLimitPos)));
                } catch (IllegalAmountException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
            database.close();
        }
    }

    @Override
    public void updateAccount(String budget, String totalLimit, String monthLimit, boolean connectedToInternet) {
        if(connectedToInternet){
            new POSTUpdateAccount(this).execute(budget, totalLimit, monthLimit);
        }else{
            transactionDBOpenHelper = new TransactionDBOpenHelper(presenter.getContext());
            database = transactionDBOpenHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(TransactionDBOpenHelper.ACCOUNT_AMOUNT, Double.parseDouble(budget));
            values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT, Double.parseDouble(totalLimit));
            values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT, Double.parseDouble(monthLimit));

            database.update(TransactionDBOpenHelper.ACCOUNT_TABLE, values, TransactionDBOpenHelper.ACCOUNT_ID + " = ?", new String[] {TransactionInteractor.getApiKey()});
            database.close();

            getAccountDetails(false);
        }
    }

    @Override
    public void onSearchDone(AccountModel account) {
        transactionDBOpenHelper = new TransactionDBOpenHelper(presenter.getContext());
        database = transactionDBOpenHelper.getWritableDatabase();
        if(isDatabaseEmpty(database)){
            ContentValues values = new ContentValues();
            values.put(TransactionDBOpenHelper.ACCOUNT_ID, TransactionInteractor.getApiKey());
            values.put(TransactionDBOpenHelper.ACCOUNT_AMOUNT, account.getBudget());
            values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT, account.getTotalLimit());
            values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT, account.getMonthLimit());

            database.insert(TransactionDBOpenHelper.ACCOUNT_TABLE, null, values);
            database.close();
        }
        presenter.fetchedAccountDetails(account);
    }

    @Override
    public void onUpdateDone(AccountModel account) {
        presenter.fetchedAccountDetails(account);
    }
}
