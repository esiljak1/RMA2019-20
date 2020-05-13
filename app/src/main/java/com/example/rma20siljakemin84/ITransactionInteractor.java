package com.example.rma20siljakemin84;

import java.util.ArrayList;

public interface ITransactionInteractor {
    ArrayList<TransactionModel> getTransactions();
    void update(TransactionModel transactionModel);
    void delete(TransactionModel transactionModel);
    void setTransactions(ArrayList<TransactionModel> transactions);
}
