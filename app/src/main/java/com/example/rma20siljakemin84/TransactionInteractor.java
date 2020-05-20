package com.example.rma20siljakemin84;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TransactionInteractor implements ITransactionInteractor, GETFilteredTransactions.OnTransactionSearchDone,
                                                                      POSTTransaction.OnTransactionPostDone,
                                                                      POSTTransactionUpdate.OnTransactionUpdateDone {
    public static final String ROOT = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com";
    public static final String API_KEY = "6e8e09ce-5c99-4f3d-a9bd-d0d60b65a5d3";
    private TransactionModel model = new TransactionModel();
    private ArrayList<TransactionModel> transactions = new ArrayList<>();
    private TransactionPresenter presenter;

    public TransactionPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    public TransactionInteractor(){
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

    public void getFilteredTransactions(String transactionTypeId, String sort, String month, String year){
        new GETFilteredTransactions(this).execute(transactionTypeId, sort, month, year);
    }

    @Override
    public void addTransaction(String... strings) {
        new POSTTransaction(this).execute(strings);
    }

    @Override
    public void onPostDone(TransactionModel transaction) {
        presenter.dodanaTransakcija(transaction);
    }

    @Override
    public void updateTransaction(String... strings) {
        new POSTTransactionUpdate(this).execute(strings);
    }

    @Override
    public void deleteTransaction(int id) {
        new DELETETransaction().execute(id);
    }

    @Override
    public void OnUpdateDone(TransactionModel transaction) {
        presenter.dodanaTransakcija(transaction);
    }
}
