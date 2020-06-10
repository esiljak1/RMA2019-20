package com.example.rma20siljakemin84;

public interface ITransactionView {
    void notifyTransactionsChanged(boolean fromMainActivity);
    void notifyAddedTransaction(TransactionModel transaction);
}
