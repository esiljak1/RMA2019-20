package com.example.rma20siljakemin84;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TransactionPresenter implements ITransactionPresenter {
    private MainActivity view;
    private TransactionModel model;

    private ArrayList<TransactionModel> currentDateTransactions = new ArrayList<>();

    public TransactionPresenter(MainActivity view) {
        this.view = view;
        this.model = new TransactionModel();
    }

    public Date getDate() {
        return model.getDate();
    }

    public void setDate(Date date) {
        model.setDate(date);
    }

    public double getAmount() {
        return model.getAmount();
    }

    public void setAmount(double amount) {
        model.setAmount(amount);
    }

    public String getTitle() {
        return model.getTitle();
    }

    public void setTitle(String title) {
        model.setTitle(title);
    }

    public Type getType() {
        return model.getType();
    }

    public void setType(Type type) {
        model.setType(type);
    }

    public String getItemDescription() {
        return model.getItemDescription();
    }

    public void setItemDescription(String itemDescription) {
        model.setItemDescription(itemDescription);
    }

    public int getTransactionInterval() {
        return model.getTransactionInterval();
    }

    public void setTransactionInterval(int transactionInterval) {
        model.setTransactionInterval(transactionInterval);
    }

    public Date getEndDate() {
        return model.getEndDate();
    }

    public void setEndDate(Date endDate) {
        model.setEndDate(endDate);
    }

    public void transactionsForCurrentDate(Date date){
        currentDateTransactions = new ArrayList<>();
        for(TransactionModel t : model.getTransactions()){
            if(t.getDate().getMonth() == date.getMonth() && t.getDate().getYear() - 1900 == date.getYear()){
                currentDateTransactions.add(t);
            }
        }
    }
    public void sort(String s) {
        if (s.equals("Sort by")) return;
        String[] temp = s.split("-");
        Comparator<TransactionModel> comparator;
        if (temp[0].equals("Price ")) {
            if (temp[1].equals(" Ascending")) {
                comparator = new Comparator<TransactionModel>() {
                    @Override
                    public int compare(TransactionModel o1, TransactionModel o2) {
                        return Double.compare(o1.getAmount(), o2.getAmount());
                    }
                };
            } else {
                comparator = new Comparator<TransactionModel>() {
                    @Override
                    public int compare(TransactionModel o1, TransactionModel o2) {
                        return Double.compare(o2.getAmount(), o1.getAmount());
                    }
                };
            }
            Collections.sort(currentDateTransactions, comparator);
        } else if (temp[0].equals("Title")) {
            if (temp[1].equals(" Ascending")) {
                comparator = new Comparator<TransactionModel>() {
                    @Override
                    public int compare(TransactionModel o1, TransactionModel o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                };
            } else {
                comparator = new Comparator<TransactionModel>() {
                    @Override
                    public int compare(TransactionModel o1, TransactionModel o2) {
                        return o2.getTitle().compareTo(o1.getTitle());
                    }
                };

            }
            Collections.sort(currentDateTransactions, comparator);
        } else {
            if (temp[1].equals(" Ascending")) {
                comparator = new Comparator<TransactionModel>() {
                    @Override
                    public int compare(TransactionModel o1, TransactionModel o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                };
            } else {
                comparator = new Comparator<TransactionModel>() {
                    @Override
                    public int compare(TransactionModel o1, TransactionModel o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                };
            }
            Collections.sort(currentDateTransactions, comparator);
        }
    }
    public  void filter(Date date, Type type){
        transactionsForCurrentDate(date);
        if(type.equals(Type.Dummy)) return;
        ArrayList<TransactionModel> temp = new ArrayList<>();
        for(TransactionModel t : currentDateTransactions){
            if(t.getType().equals(type)){
                temp.add(t);
            }
        }currentDateTransactions = temp;
    }
    public void addTransaction(Date date, double amount, String title, Type type, String itemDescription, int transactionInterval, Date endDate){
    }
    public void updateTransaction(TransactionModel newTransaction){
        model.removeTransaction(newTransaction);
        model.addTransaction(newTransaction);
    }

    public ArrayList<TransactionModel> getCurrentDateTransactions() {
        return currentDateTransactions;
    }

    public void setCurrentDateTransactions(ArrayList<TransactionModel> currentDateTransactions) {
        this.currentDateTransactions = currentDateTransactions;
    }
}
