package com.example.rma20siljakemin84;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TransactionInteractor implements ITransactionInteractor, GETFilteredTransactions.OnTransactionSearchDone,
                                                                      POSTTransaction.OnTransactionPostDone,
                                                                      POSTTransactionUpdate.OnTransactionUpdateDone {
    private static String ROOT;
    private static String API_KEY;
    private TransactionModel model = new TransactionModel();
    private ArrayList<TransactionModel> transactions = new ArrayList<>();
    private TransactionPresenter presenter;

    private TransactionDBOpenHelper transactionDBOpenHelper;
    private SQLiteDatabase database;

    private boolean isInDatabaseTable(SQLiteDatabase database, String name, Integer id){
        String query = "SELECT * FROM " + name + " WHERE " + TransactionDBOpenHelper.TRANSACTION_ID + " = " + id;
        Cursor cursor = database.rawQuery(query, null);
        return cursor.getCount() != 0;
    }

    private TransactionModel getTransactionFromStrings(String ... strings){
        Calendar date = Calendar.getInstance(), endDate = null;
        try {
            date.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(strings[0]));
            if(!strings[3].equals("")){
                endDate = Calendar.getInstance();
                endDate.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(strings[3]));
            }
            Double amount = Double.parseDouble(strings[2]);
            int typeId = Integer.parseInt(strings[6]);

            String itemDescription = null;
            if(strings[4] != ""){
                itemDescription = strings[4];
            }

            int transactionInterval = 0;
            if(!strings[5].equals("")){
                transactionInterval = Integer.parseInt(strings[5]);
            }
            return new TransactionModel(date, amount, strings[1], Type.fromId(typeId), itemDescription, transactionInterval, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertDateToString(Date date){
        if(date == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    public TransactionPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    public TransactionInteractor(String root, String api){
        ROOT = root;
        API_KEY = api;
    }

    public static String getROOT() {
        return ROOT;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static String convertStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }

    public ArrayList<TransactionModel> getTransactions(){
        return transactions;
    }

    public void update(TransactionModel newTransaction){
        transactions.remove(newTransaction);
        transactions.add(newTransaction);
    }
    public void delete(TransactionModel transaction){
        model.removeTransaction(transaction);
    }

    @Override
    public void setTransactions(ArrayList<TransactionModel> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void onGetDone(ArrayList<TransactionModel> transactions) {
        presenter.filtriraneTransakcije(transactions);
    }

    public void getFilteredTransactions(String transactionTypeId, String sort, String month, String year, boolean isConnectedToInternet){
        if(isConnectedToInternet) {
            new GETFilteredTransactions(this).execute(transactionTypeId, sort, month, year);
        }else{
            onGetDone(transactions);
        }
    }

    @Override
    public void addTransaction(boolean isConnectedToInternet, String... strings) {
        if(isConnectedToInternet) {
            new POSTTransaction(this).execute(strings);
        }else{
            transactionDBOpenHelper = new TransactionDBOpenHelper(((Context) presenter.getView()));
            database = transactionDBOpenHelper.getWritableDatabase();

            TransactionModel transactionModel = getTransactionFromStrings(strings);
            ContentValues values = new ContentValues();

            values.put(TransactionDBOpenHelper.TRANSACTION_DATE, convertDateToString(transactionModel.getDate().getTime()));
            values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT, transactionModel.getAmount());
            values.put(TransactionDBOpenHelper.TRANSACTION_TITLE, transactionModel.getTitle());
            values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE, convertDateToString(transactionModel.getEndDate().getTime()));
            values.put(TransactionDBOpenHelper.TRANSACTION_INTERVAL, transactionModel.getTransactionInterval());
            values.put(TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION, transactionModel.getItemDescription());
            values.put(TransactionDBOpenHelper.TRANSACTION_TYPE, transactionModel.getType().getValue());

            database.insert(TransactionDBOpenHelper.CREATED_TRANSACTIONS_TABLE, null, values);
            database.close();

            transactions.add(transactionModel);
        }
    }

    @Override
    public void onPostDone(TransactionModel transaction) {
        presenter.dodanaTransakcija(transaction);
    }

    @Override
    public void updateTransaction(boolean isConnectedToInternet, String... strings) {
        if(isConnectedToInternet) {
            new POSTTransactionUpdate(this).execute(strings);
        }
    }

    @Override
    public void deleteTransaction(int id, boolean isConnectedToInternet) {
        if(isConnectedToInternet) {
            new DELETETransaction().execute(id);
        }
    }

    @Override
    public void OnUpdateDone(TransactionModel transaction) {
        presenter.dodanaTransakcija(transaction);
    }
}
