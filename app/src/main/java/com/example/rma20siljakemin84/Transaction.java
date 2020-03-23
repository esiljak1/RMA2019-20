package com.example.rma20siljakemin84;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction {
    private Date date;
    private double amount;
    private String title;
    private Type type;
    private String itemDescription;
    private int transactionInterval;
    private Date endDate;

    public Transaction(Date date, double amount, String title, Type type, String itemDescription, int transactionInterval, Date endDate) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
    }
    public static List<Transaction> napuni(){
        List<Transaction> ret = new ArrayList<>();

        //Individual payments
        ret.add(new Transaction(new Date(2020, 3, 22), 150, "Transaction 1", Type.INDIVIDUALPAYMENT, "A very nice jacket", 0, null));
        ret.add(new Transaction(new Date(2020, 1, 7), 79.90, "Transaction 2", Type.INDIVIDUALPAYMENT, "Steam wallet", 0, null));
        ret.add(new Transaction(new Date(2020, 1, 30), 100, "Transaction 3", Type.INDIVIDUALPAYMENT, "4GB DDR4 RAM", 0, null));
        ret.add(new Transaction(new Date(2019, 11, 25), 35, "Transaction 4", Type.INDIVIDUALPAYMENT, "A very cool hat", 0, null));

        //Regular payments
        ret.add(new Transaction(new Date(2019, 1, 15), 50, "Transaction 5", Type.REGULARPAYMENT, "Electricity bill", 30, new Date(2021, 1, 15)));
        ret.add(new Transaction(new Date(2019, 2, 1), 100, "Transaction 6", Type.REGULARPAYMENT, "Heating bill", 30, new Date(2024, 2, 1)));
        ret.add(new Transaction(new Date(2018, 12, 10), 35, "Transaction 7", Type.REGULARPAYMENT, "Water bill", 30, new Date(2028, 12, 10)));
        ret.add(new Transaction(new Date(2020, 1, 3), 50, "Transaction 8", Type.REGULARPAYMENT, "Gym subscription", 30, new Date(2021, 1, 3)));

        //Purchase
        ret.add(new Transaction(new Date(2020, 1, 2), 25, "Transaction 9", Type.PURCHASE, "Sunglasses", 0, null));
        ret.add(new Transaction(new Date(2019, 8, 16), 250, "Transaction 10", Type.PURCHASE, "A spoon", 0, null));
        ret.add(new Transaction(new Date(2020, 2, 15), 5, "Transaction 11", Type.PURCHASE, "A plate", 0, null));
        ret.add(new Transaction(new Date(2020, 1, 7), 120, "Transaction 12", Type.PURCHASE, "Sports shoes", 0, null));

        //Individual income
        ret.add(new Transaction(new Date(2020, 2, 1), 250, "Transaction 13", Type.INDIVIDUALINCOME, null, 0, null));
        ret.add(new Transaction(new Date(2020, 1, 6), 500, "Transaction 14", Type.INDIVIDUALINCOME, null, 0, null));
        ret.add(new Transaction(new Date(2019, 12, 1), 1500, "Transaction 15", Type.INDIVIDUALINCOME, null, 0, null));
        ret.add(new Transaction(new Date(2020, 3, 1), 500, "Transaction 16", Type.INDIVIDUALINCOME, null, 0, null));

        //Regular income
        ret.add(new Transaction(new Date(2019, 1, 7), 100, "Transaction 17", Type.REGULARINCOME, null, 365, new Date(2025, 1, 7)));
        ret.add(new Transaction(new Date(2019, 5, 15), 50, "Transaction 18", Type.REGULARINCOME, null, 16, new Date(2020, 5, 15)));
        ret.add(new Transaction(new Date(2020, 4, 1), 2000, "Transaction 19", Type.REGULARINCOME, null, 30, new Date(2021, 4, 1)));
        ret.add(new Transaction(new Date(2019, 9, 9), 10, "Transaction 20", Type.REGULARINCOME, null, 7, new Date(2021, 9, 9)));

        return ret;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
