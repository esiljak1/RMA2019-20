package com.example.rma20siljakemin84;

import android.content.Context;

import java.util.ArrayList;

public interface ITransactionInteractor {
    ArrayList<TransactionModel> getTransactions();
    void update(TransactionModel transactionModel);
    void delete(TransactionModel transactionModel);
    void setTransactions(ArrayList<TransactionModel> transactions);
    void setPresenter(TransactionPresenter presenter);
    TransactionPresenter getPresenter();
    void getFilteredTransactions(String typeId, String sort, String month, String year, boolean isConnected);
    void addTransaction(boolean isConnected, String ... strings);
    void updateTransaction(boolean isConnected, String ... strings);
    void deleteTransaction(int id, int internal_id, boolean isConnected);
    void updateFromDatabase(Context context);
    boolean isInDatabaseDeletedTable(Context context, int id);
}
