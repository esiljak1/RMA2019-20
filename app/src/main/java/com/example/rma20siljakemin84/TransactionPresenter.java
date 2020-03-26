package com.example.rma20siljakemin84;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class TransactionPresenter implements ITransactionPresenter {
    private MainActivity view;
    private TransactionModel model;

    private ArrayList<TransactionModel> currentDateTransactions = new ArrayList<>();

    public TransactionPresenter(MainActivity view) {
        this.view = view;
        this.model = new TransactionModel();
    }


    public void transactionsForCurrentDate(Calendar date){
        currentDateTransactions = new ArrayList<>();
        for(TransactionModel t : model.getTransactions()){
            if(t.getDate().get(Calendar.MONTH) == date.get(Calendar.MONTH) && t.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR)){
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
    public  void filter(Calendar date, Type type){
        transactionsForCurrentDate(date);
        if(type.equals(Type.Dummy)) return;
        ArrayList<TransactionModel> temp = new ArrayList<>();
        for(TransactionModel t : currentDateTransactions){
            if(t.getType().equals(type)){
                temp.add(t);
            }
        }currentDateTransactions = temp;
    }
    public void addTransaction(Calendar date, double amount, String title, Type type, String itemDescription, int transactionInterval, Calendar endDate){
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
