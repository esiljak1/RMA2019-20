package com.example.rma20siljakemin84;

import java.util.ArrayList;

public class TransactionInteractor implements ITransactionInteractor {
    private TransactionModel model = new TransactionModel();

    public ArrayList<TransactionModel> get(){
        return model.getTransactions();
    }

    public void update(TransactionModel newTransaction){
        model.removeTransaction(newTransaction);
        model.addTransaction(newTransaction);
    }
    public void delete(TransactionModel transaction){
        model.removeTransaction(transaction);
    }

}
