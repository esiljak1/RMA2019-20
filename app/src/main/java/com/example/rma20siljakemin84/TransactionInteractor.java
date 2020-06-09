package com.example.rma20siljakemin84;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TransactionInteractor implements ITransactionInteractor, GETFilteredTransactions.OnTransactionSearchDone,
                                                                      POSTTransaction.OnTransactionPostDone,
                                                                      POSTTransactionUpdate.OnTransactionUpdateDone {
    private static String ROOT;
    private static String API_KEY;
    private TransactionModel model = new TransactionModel();
    private ArrayList<TransactionModel> transactions = new ArrayList<>();
    private TransactionPresenter presenter;

    private Uri getCreatedUri(){
        return Uri.parse("content://rma.provider.createdTransactions/elements");
    }

    private Uri getUpdatedUri(){
        return Uri.parse("content://rma.provider.updatedTransactions/elements");
    }

    private Uri getDeletedUri(){
        return Uri.parse("content://rma.provider.deletedTransactionsTable/elements");
    }

    private boolean isInDatabaseTable(String name, Integer id){
        ContentResolver cr = presenter.getContext().getApplicationContext().getContentResolver();
        Uri adresa = null;
        if(name.equals(TransactionDBOpenHelper.CREATED_TRANSACTIONS_TABLE)){
            adresa = getCreatedUri();
        }else if(name.equals(TransactionDBOpenHelper.UPDATED_TRANSACTIONS_TABLE)){
            adresa = getUpdatedUri();
        }else{
            adresa = getDeletedUri();
        }
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID, TransactionDBOpenHelper.TRANSACTION_ID
        };

        String where = TransactionDBOpenHelper.TRANSACTION_ID + " = ?";
        String[] whereArgs = new String[]{id + ""};
        Cursor cursor = cr.query(adresa, kolone, where, whereArgs, null);
        return cursor.getCount() != 0;
    }

    private Cursor getCursorForTable(String table, Context context){
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        if(!table.equals(TransactionDBOpenHelper.DELETED_TRANSACTIONS_TABLE)){
            String[] kolone = new String[]{
                    TransactionDBOpenHelper.TRANSACTION_ID, TransactionDBOpenHelper.TRANSACTION_DATE,
                    TransactionDBOpenHelper.TRANSACTION_AMOUNT, TransactionDBOpenHelper.TRANSACTION_TITLE, TransactionDBOpenHelper.TRANSACTION_TYPE,
                    TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION, TransactionDBOpenHelper.TRANSACTION_INTERVAL, TransactionDBOpenHelper.TRANSACTION_END_DATE
            };
            Uri adresa = null;
            if(table.equals(TransactionDBOpenHelper.CREATED_TRANSACTIONS_TABLE)){
                adresa = getCreatedUri();
            }else{
                adresa = getUpdatedUri();
            }
            String where = null;
            String[] whereArgs = null;
            String order = null;
            return cr.query(adresa, kolone, where, whereArgs, order);
        }else{
            String[] kolone = new String[]{
                    TransactionDBOpenHelper.TRANSACTION_ID
            };
            Uri adresa = getDeletedUri();
            String where = null;
            String[] whereArgs = null;
            String order = null;
            return cr.query(adresa, kolone, where, whereArgs, order);
        }
    }

    private TransactionModel getTransactionFromStrings(String ... strings){
        int i = 0;
        int id = 0;
        if(strings.length == 8){
            id = Integer.parseInt(strings[i]);
            i++;
        }
        Calendar date = Calendar.getInstance(), endDate = null;
        try {
            date.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(strings[i]));
            i+=3;
            if(!strings[i].equals("")){
                endDate = Calendar.getInstance();
                endDate.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(strings[i]));
            }
            i--;
            Double amount = Double.parseDouble(strings[i]);
            i+=4;
            int typeId = Integer.parseInt(strings[i]);
            i-=2;
            String itemDescription = null;
            if(strings[i] != ""){
                itemDescription = strings[i];
            }
            i++;
            int transactionInterval = 0;
            if(!strings[i].equals("")){
                transactionInterval = Integer.parseInt(strings[i]);
            }
            i-=4;
            return new TransactionModel(id, date, amount, strings[i], Type.fromId(typeId), itemDescription, transactionInterval, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertDateToString(Calendar date){
        if(date == null) return null;
        return new SimpleDateFormat("dd.MM.yyyy").format(date.getTime());
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

            ContentResolver cr = presenter.getContext().getApplicationContext().getContentResolver();
            Uri createdTransactionsUri = getCreatedUri();

            TransactionModel transactionModel = getTransactionFromStrings(strings);
            ContentValues values = new ContentValues();

            values.put(TransactionDBOpenHelper.TRANSACTION_DATE, convertDateToString(transactionModel.getDate()));
            values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT, transactionModel.getAmount());
            values.put(TransactionDBOpenHelper.TRANSACTION_TITLE, transactionModel.getTitle());
            if(convertDateToString(transactionModel.getEndDate()) != null){
                values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE, convertDateToString(transactionModel.getEndDate()));
            }
            values.put(TransactionDBOpenHelper.TRANSACTION_INTERVAL, transactionModel.getTransactionInterval());
            if(transactionModel.getItemDescription() != null) {
                values.put(TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION, transactionModel.getItemDescription());
            }
            values.put(TransactionDBOpenHelper.TRANSACTION_TYPE, transactionModel.getType().getValue());

            cr.insert(createdTransactionsUri, values);

            transactions.add(transactionModel);
            presenter.dodanaTransakcija(transactionModel);
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
        }else{
            ContentResolver cr = presenter.getContext().getApplicationContext().getContentResolver();
            Uri updateTransactionUri = getUpdatedUri();

            ContentValues values = new ContentValues();
            TransactionModel transactionModel = getTransactionFromStrings(strings);

            values.put(TransactionDBOpenHelper.TRANSACTION_ID, transactionModel.getId());
            values.put(TransactionDBOpenHelper.TRANSACTION_DATE, convertDateToString(transactionModel.getDate()));
            values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT, transactionModel.getAmount());
            values.put(TransactionDBOpenHelper.TRANSACTION_TITLE, transactionModel.getTitle());
            if(convertDateToString(transactionModel.getEndDate()) != null){
                values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE, convertDateToString(transactionModel.getEndDate()));
            }
            values.put(TransactionDBOpenHelper.TRANSACTION_INTERVAL, transactionModel.getTransactionInterval());
            if(transactionModel.getItemDescription() != null) {
                values.put(TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION, transactionModel.getItemDescription());
            }
            values.put(TransactionDBOpenHelper.TRANSACTION_TYPE, transactionModel.getType().getValue());

            if(isInDatabaseTable(TransactionDBOpenHelper.UPDATED_TRANSACTIONS_TABLE, transactionModel.getId())){
                cr.update(updateTransactionUri, values, TransactionDBOpenHelper.TRANSACTION_ID + " = ?"
                        , new String[]{transactionModel.getId() + ""});
            }else {
                cr.insert(updateTransactionUri, values);
            }

            transactions.remove(transactionModel);
            transactions.add(transactionModel);

            presenter.dodanaTransakcija(transactionModel);
        }
    }

    @Override
    public void deleteTransaction(int id, boolean isConnectedToInternet) {
        if(isConnectedToInternet) {
            new DELETETransaction().execute(id);
        }else{
            ContentResolver cr = presenter.getContext().getApplicationContext().getContentResolver();
            Uri deletedTransactionsUri = getDeletedUri();

            ContentValues values = new ContentValues();
            values.put(TransactionDBOpenHelper.TRANSACTION_ID, id);

            if(isInDatabaseTable(TransactionDBOpenHelper.DELETED_TRANSACTIONS_TABLE, id)){
                cr.update(deletedTransactionsUri, values, TransactionDBOpenHelper.TRANSACTION_ID + " = ?", new String[]{id + ""});
            }else{
                cr.insert(deletedTransactionsUri, values);
            }
        }
    }

    @Override
    public void OnUpdateDone(TransactionModel transaction) {
        presenter.dodanaTransakcija(transaction);
    }

    @Override
    public void updateFromDatabase(Context context) {
        Cursor cursor = getCursorForTable(TransactionDBOpenHelper.CREATED_TRANSACTIONS_TABLE, context);
        if(cursor.moveToFirst()){
            int titlePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE);
            int amountPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT);
            int datePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DATE);
            int endDatePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_END_DATE);
            int descriptionPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION);
            int intervalPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERVAL);
            int typePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE);
            do{
                addTransaction(true, cursor.getString(datePos), cursor.getString(titlePos), cursor.getDouble(amountPos) + "",
                        cursor.getString(endDatePos), cursor.getString(descriptionPos), cursor.getInt(intervalPos) + "", cursor.getInt(typePos) + "");
            }while(cursor.moveToNext());
        }

        cursor = getCursorForTable(TransactionDBOpenHelper.UPDATED_TRANSACTIONS_TABLE, context);
        if(cursor.moveToFirst()){
            int idPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ID);
            int titlePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE);
            int amountPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT);
            int datePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DATE);
            int endDatePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_END_DATE);
            int descriptionPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION);
            int intervalPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERVAL);
            int typePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE);
            do{
                updateTransaction(true, cursor.getInt(idPos) + "", cursor.getString(datePos), cursor.getString(titlePos),
                        cursor.getDouble(amountPos) + "", cursor.getString(endDatePos), cursor.getString(descriptionPos),
                        cursor.getInt(intervalPos) + "", cursor.getInt(typePos) + "");
            }while(cursor.moveToNext());
        }

        cursor = getCursorForTable(TransactionDBOpenHelper.DELETED_TRANSACTIONS_TABLE, context);
        if(cursor.moveToFirst()){
            int idPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ID);
            do{
                deleteTransaction(cursor.getInt(idPos), true);
            }while (cursor.moveToNext());
        }
        cursor.close();

        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri createdTransactionsUri = getCreatedUri(),
                updatedTransactionsUri = getUpdatedUri(),
                deletedTransactionsUri = getDeletedUri();
        cr.delete(createdTransactionsUri, null, null);
        cr.delete(updatedTransactionsUri, null, null);
        cr.delete(deletedTransactionsUri, null, null);
    }

    public boolean isInDatabaseDeletedTable(Context context, int id){
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri deletedTransactionsUri = getDeletedUri();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID, TransactionDBOpenHelper.TRANSACTION_ID
        };

        Cursor cursor = cr.query(deletedTransactionsUri, kolone, null, null, null);
        return cursor.getCount() != 0;
    }
}
