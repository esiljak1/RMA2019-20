package com.example.rma20siljakemin84;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AccountInteractor implements IAccountInteractor, GETAccountDetails.OnAccountSearchDone, POSTUpdateAccount.OnAccountUpdateDone {
    private AccountModel model = AccountModel.getInstance();
    private AccountPresenter presenter;

    private Uri getAccountUri(){
        return Uri.parse("content://rma.provider.account/elements");
    }

    private boolean isDatabaseEmpty(){
        ContentResolver cr = presenter.getContext().getApplicationContext().getContentResolver();
        Uri adresa = getAccountUri();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID, TransactionDBOpenHelper.ACCOUNT_ID
        };

        Cursor cursor = cr.query(adresa, kolone, null, null, null);
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
            ContentResolver cr = presenter.getContext().getApplicationContext().getContentResolver();
            Uri adresa = getAccountUri();
            String[] kolone = new String[]{
                    TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID, TransactionDBOpenHelper.ACCOUNT_ID,
                    TransactionDBOpenHelper.ACCOUNT_AMOUNT, TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT, TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT
            };
            String order = TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID;

            Cursor cursor = cr.query(adresa, kolone, null, null, order);
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
        }
    }

    @Override
    public void updateAccount(String budget, String totalLimit, String monthLimit, boolean connectedToInternet) {
        if(connectedToInternet){
            new POSTUpdateAccount(this).execute(budget, totalLimit, monthLimit);
        }else{

            ContentResolver cr = presenter.getContext().getApplicationContext().getContentResolver();
            Uri adresa = getAccountUri();

            ContentValues values = new ContentValues();
            values.put(TransactionDBOpenHelper.ACCOUNT_ID, TransactionInteractor.getApiKey());
            values.put(TransactionDBOpenHelper.ACCOUNT_AMOUNT, Double.parseDouble(budget));
            values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT, Double.parseDouble(totalLimit));
            values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT, Double.parseDouble(monthLimit));

            if(isDatabaseEmpty()){
                cr.insert(adresa, values);
            }else{
                cr.update(adresa, values, TransactionDBOpenHelper.ACCOUNT_ID + " = ?", new String[]{TransactionInteractor.getApiKey()});
            }
            getAccountDetails(false);
        }
    }

    @Override
    public void onSearchDone(AccountModel account) {
        presenter.fetchedAccountDetails(account);
    }

    @Override
    public void onUpdateDone(AccountModel account) {
        presenter.fetchedAccountDetails(account);
    }

    public void updateFromDatabase(Context context){
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri adresa = getAccountUri();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID, TransactionDBOpenHelper.ACCOUNT_ID, TransactionDBOpenHelper.ACCOUNT_AMOUNT,
                TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT, TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT
        };

        Cursor cursor = cr.query(adresa, kolone, null, null, null);

        if(cursor.moveToFirst()){
            int amountPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_AMOUNT);
            int totalLimitPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT);
            int monthLimitPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT);

            updateAccount(cursor.getDouble(amountPos) + "", cursor.getDouble(totalLimitPos) + "", cursor.getDouble(monthLimitPos) + "", true);
        }
        cursor.close();

        cr.delete(adresa, null, null);
    }
}
