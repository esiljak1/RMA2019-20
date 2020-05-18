package com.example.rma20siljakemin84;

public interface ITransactionView {
    void notifyTransactionsChanged();
    void notifyAddedTransaction(TransactionModel transaction);
}
