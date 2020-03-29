package com.example.rma20siljakemin84;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class TransactionPresenter implements ITransactionPresenter {
    private MainActivity view;
    private TransactionInteractor interactor;
    private AccountPresenter account;

    private ArrayList<TransactionModel> currentDateTransactions = new ArrayList<>();

    public TransactionPresenter(MainActivity view) {
        this.view = view;
        this.interactor = new TransactionInteractor();
        this.account = new AccountPresenter(view);
    }

    private boolean checkRegular(TransactionModel transaction, Calendar date){
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.DATE, transaction.getDate().get(Calendar.DATE));
        temp.set(Calendar.MONTH, transaction.getDate().get(Calendar.MONTH));
        temp.set(Calendar.YEAR, transaction.getDate().get(Calendar.YEAR));
        while (temp.compareTo(transaction.getEndDate()) < 0){
            if(temp.get(Calendar.MONTH) == date.get(Calendar.MONTH) && temp.get(Calendar.YEAR) == date.get(Calendar.YEAR)){
                return true;
            }temp.add(Calendar.DATE, transaction.getTransactionInterval());
        }return false;
    }


    public void transactionsForCurrentDate(Calendar date){
        currentDateTransactions = new ArrayList<>();
        boolean uslov = true;
        for(TransactionModel t : interactor.get()){
            if(t.getType().equals(Type.REGULARINCOME) || t.getType().equals(Type.REGULARPAYMENT)){
                if(checkRegular(t, date)) currentDateTransactions.add(t);
            }
            else if(uslov && t.getDate().get(Calendar.MONTH) == date.get(Calendar.MONTH) && t.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR)){
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
    public void updateTransaction(TransactionModel newTransaction){
        interactor.update(newTransaction);
    }
    public void deleteTransaction(TransactionModel transaction){
        interactor.delete(transaction);
    }

    public ArrayList<TransactionModel> getCurrentDateTransactions() {
        return currentDateTransactions;
    }

    public void setCurrentDateTransactions(ArrayList<TransactionModel> currentDateTransactions) {
        this.currentDateTransactions = currentDateTransactions;
    }

    public double getAmountforDate(Calendar date){
        double ret = 0;
        for(TransactionModel tm : interactor.get()){
            if(tm.getType().equals(Type.REGULARPAYMENT) || tm.getType().equals(Type.REGULARINCOME)){
                if(checkRegular(tm, date)){
                    if(tm.getType().equals(Type.REGULARPAYMENT)) {
                        ret += tm.getAmount();
                    }else{
                        ret -= tm.getAmount();
                    }
                }
            }else if(date.get(Calendar.MONTH) == tm.getDate().get(Calendar.MONTH) && date.get(Calendar.YEAR) == tm.getDate().get(Calendar.YEAR)) {
                if(tm.getType().equals(Type.INDIVIDUALINCOME)){
                    ret -= tm.getAmount();
                }else{
                    ret += tm.getAmount();
                }
            }
        }
        return ret;
    }

    public double getAllAmounts(){
        double ret = 0;
        for(TransactionModel tm : interactor.get()){
            if(tm.getType().equals(Type.INDIVIDUALINCOME) || tm.getType().equals(Type.REGULARINCOME)){
                ret -= tm.getAmount();
            }else{
                ret += tm.getAmount();
            }
        }
        return ret;
    }

    public void updateAccountBudget(double iznos) {
        account.updateBudget(iznos);
    }
    public AccountPresenter getAccount() {
        return account;
    }

    public void setAccount(AccountPresenter account) {
        this.account = account;
    }
}
