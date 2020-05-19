package com.example.rma20siljakemin84;

import java.util.ArrayList;

public interface ITransactionInteractor {
    ArrayList<TransactionModel> getTransactions();
    void update(TransactionModel transactionModel);
    void delete(TransactionModel transactionModel);
    void setTransactions(ArrayList<TransactionModel> transactions);
    void setPresenter(TransactionPresenter presenter);
    TransactionPresenter getPresenter();
    void getFilteredTransactions(String typeId, String sort, String month, String year);
    void addTransaction(String ... strings);
    void updateTransaction(String ... strings);
}
