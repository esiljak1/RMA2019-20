package com.example.rma20siljakemin84;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

public class TransactionModel implements ITransactionModel {
    private static int maxId = 1;
    private int id = 0;
    private Calendar date;
    private double amount = 0;
    private String title = "";
    private Type type = Type.INDIVIDUALPAYMENT;
    private String itemDescription = "";
    private int transactionInterval = 0;
    private Calendar endDate;

    private static ArrayList<TransactionModel> transactions = new ArrayList<TransactionModel>(){
        {
            //Individual payments
            Calendar cal = Calendar.getInstance(), end = Calendar.getInstance();
            cal.set(2020, 3, 22);
            add(new TransactionModel(cal, 150, "Transaction 1", Type.INDIVIDUALPAYMENT, "A very nice jacket", 0, null));
            cal = Calendar.getInstance();
            cal.set(2020,1,7);
            add(new TransactionModel(cal, 79.90, "Transaction 2", Type.INDIVIDUALPAYMENT, "Steam wallet", 0, null));
            cal = Calendar.getInstance();
            cal.set(2020, 1, 30);
            add(new TransactionModel(cal, 100, "Transaction 3", Type.INDIVIDUALPAYMENT, "4GB DDR4 RAM", 0, null));
            cal = Calendar.getInstance();
            cal.set(2019, 11, 25);
            add(new TransactionModel(cal, 35, "Transaction 4", Type.INDIVIDUALPAYMENT, "A very cool hat", 0, null));

            //Regular payments
            cal = Calendar.getInstance();
            end = Calendar.getInstance();
            cal.set(2019, 1, 15);
            end.set(2021, 1, 15);
            add(new TransactionModel(cal, 50, "Transaction 5", Type.REGULARPAYMENT, "Electricity bill", 30, end));
            end = Calendar.getInstance();
            cal = Calendar.getInstance();
            cal.set(2019, 2, 1);
            end.set(2024, 2, 1);
            add(new TransactionModel(cal, 100, "Transaction 6", Type.REGULARPAYMENT, "Heating bill", 30, end));
            end = Calendar.getInstance();
            cal = Calendar.getInstance();
            cal.set(2018, 12, 10);
            end.set(2020, 12, 10);
            add(new TransactionModel(cal, 35, "Transaction 7", Type.REGULARPAYMENT, "Water bill", 30, end));
            end = Calendar.getInstance();
            cal = Calendar.getInstance();
            cal.set(2020, 1, 3);
            end.set(2021, 1, 3);
            add(new TransactionModel(cal, 50, "Transaction 8", Type.REGULARPAYMENT, "Gym subscription", 30, end));

            //Purchase
            cal = Calendar.getInstance();
            cal.set(2020, 1, 2);
            add(new TransactionModel(cal, 25, "Transaction 9", Type.PURCHASE, "Sunglasses", 0, null));
            cal = Calendar.getInstance();
            cal.set(2019, 8, 16);
            add(new TransactionModel(cal, 250, "Transaction 10", Type.PURCHASE, "A spoon", 0, null));
            cal = Calendar.getInstance();
            cal.set(2020, 2, 15);
            add(new TransactionModel(cal, 5, "Transaction 11", Type.PURCHASE, "A plate", 0, null));
            cal = Calendar.getInstance();
            cal.set(2020, 1, 7);
            add(new TransactionModel(cal, 120, "Transaction 12", Type.PURCHASE, "Sports shoes", 0, null));

            //Individual income
            cal = Calendar.getInstance();
            cal.set(2020, 2, 1);
            add(new TransactionModel(cal, 250, "Transaction 13", Type.INDIVIDUALINCOME, null, 0, null));
            cal = Calendar.getInstance();
            cal.set(2020, 1, 6);
            add(new TransactionModel(cal, 500, "Transaction 14", Type.INDIVIDUALINCOME, null, 0, null));
            cal = Calendar.getInstance();
            cal.set(2019, 12, 1);
            add(new TransactionModel(cal, 1500, "Transaction 15", Type.INDIVIDUALINCOME, null, 0, null));
            cal = Calendar.getInstance();
            cal.set(2020, 3, 1);
            add(new TransactionModel(cal, 500, "Transaction 16", Type.INDIVIDUALINCOME, null, 0, null));

            //Regular income
            end = Calendar.getInstance();
            cal = Calendar.getInstance();
            cal.set(2019, 1, 7);
            end.set(2025, 1, 7);
            add(new TransactionModel(cal, 100, "Transaction 17", Type.REGULARINCOME, null, 365, end));
            end = Calendar.getInstance();
            cal = Calendar.getInstance();
            cal.set(2019, 5, 15);
            end.set(2020, 5, 15);
            add(new TransactionModel(cal, 50, "Transaction 18", Type.REGULARINCOME, null, 16, end));
            end = Calendar.getInstance();
            cal = Calendar.getInstance();
            cal.set(2019, 4, 1);
            end.set(2021, 4, 1);
            add(new TransactionModel(cal, 2000, "Transaction 19", Type.REGULARINCOME, null, 30, end));
            end = Calendar.getInstance();
            cal = Calendar.getInstance();
            cal.set(2019, 9, 9);
            end.set(2021, 9, 9);
            add(new TransactionModel(cal, 10, "Transaction 20", Type.REGULARINCOME, null, 7, end));
        }
    };

    public TransactionModel() {
    }

    public TransactionModel(Calendar date, double amount, String title, Type type, String itemDescription, int transactionInterval, Calendar endDate) {
        this.id = maxId++;
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title.length() <= 3 || title.length() >= 15) throw new IllegalArgumentException("Title must be between 3 and 15 characters long");
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getTransactionInterval() {
        return transactionInterval;
    }

    public void setTransactionInterval(int transactionInterval) {
        this.transactionInterval = transactionInterval;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public void removeTransaction(TransactionModel t){
        transactions.remove(t);
    }
    public void addTransaction(TransactionModel t){
        transactions.add(t);
    }

    public ArrayList<TransactionModel> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<TransactionModel> transactions) {
        this.transactions = transactions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof TransactionModel){
            return id == ((TransactionModel) obj).id;
        }
        return false;
    }
}
