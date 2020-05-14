package com.example.rma20siljakemin84;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class TransactionPresenter implements ITransactionPresenter, Parcelable, TransactionInteractor.OnTransactionSearchDone {
    private ITransactionView view;
    private ITransactionInteractor interactor;
    private IAccountPresenter account;

    private ArrayList<TransactionModel> currentDateTransactions = new ArrayList<>();

    public TransactionPresenter(ITransactionView view) {
        this.view = view;
        this.interactor = new TransactionInteractor();
        this.account = new AccountPresenter(((IAccountView) view));
    }

    protected TransactionPresenter(Parcel in) {
        account = in.readParcelable(AccountPresenter.class.getClassLoader());
    }

    public static final Creator<TransactionPresenter> CREATOR = new Creator<TransactionPresenter>() {
        @Override
        public TransactionPresenter createFromParcel(Parcel in) {
            return new TransactionPresenter(in);
        }

        @Override
        public TransactionPresenter[] newArray(int size) {
            return new TransactionPresenter[size];
        }
    };

    private int checkRegular(TransactionModel transaction, Calendar date){
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.DATE, transaction.getDate().get(Calendar.DATE));
        temp.set(Calendar.MONTH, transaction.getDate().get(Calendar.MONTH));
        temp.set(Calendar.YEAR, transaction.getDate().get(Calendar.YEAR));
        int brojPonavljanjaUMjesecu = 0;
        while (temp.get(Calendar.YEAR) <= date.get(Calendar.YEAR) && temp.get(Calendar.MONTH) <= date.get(Calendar.MONTH)){
            if(transaction.getEndDate() != null && temp.compareTo(transaction.getEndDate()) > 0){
                return brojPonavljanjaUMjesecu;
            }
            if(temp.get(Calendar.MONTH) == date.get(Calendar.MONTH) && temp.get(Calendar.YEAR) == date.get(Calendar.YEAR)){
                brojPonavljanjaUMjesecu++;
            }temp.add(Calendar.DATE, transaction.getTransactionInterval());
        }return brojPonavljanjaUMjesecu;
    }

    private boolean checkRegularWeek(TransactionModel transaction, Calendar date){
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.DATE, transaction.getDate().get(Calendar.DATE));
        temp.set(Calendar.MONTH, transaction.getDate().get(Calendar.MONTH));
        temp.set(Calendar.YEAR, transaction.getDate().get(Calendar.YEAR));
        while(temp.get(Calendar.WEEK_OF_MONTH) <= date.get(Calendar.WEEK_OF_MONTH) && temp.get(Calendar.MONTH) <= date.get(Calendar.MONTH) && temp.get(Calendar.YEAR) <= date.get(Calendar.YEAR)){
            if(transaction.getEndDate() != null && temp.compareTo(transaction.getEndDate()) > 0){
                return false;
            }
            if(temp.get(Calendar.WEEK_OF_MONTH) == date.get(Calendar.WEEK_OF_MONTH) && temp.get(Calendar.MONTH) == date.get(Calendar.MONTH) && temp.get(Calendar.YEAR) == date.get(Calendar.YEAR)){
                return true;
            }
            temp.add(Calendar.DATE, transaction.getTransactionInterval());
        }return false;
    }

    private boolean checkRegularDay(TransactionModel transaction, Calendar date){
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.DATE, transaction.getDate().get(Calendar.DATE));
        temp.set(Calendar.MONTH, transaction.getDate().get(Calendar.MONTH));
        temp.set(Calendar.YEAR, transaction.getDate().get(Calendar.YEAR));
        while(temp.get(Calendar.DAY_OF_MONTH) <= date.get(Calendar.DAY_OF_MONTH) && temp.get(Calendar.MONTH) <= date.get(Calendar.MONTH) && temp.get(Calendar.YEAR) <= date.get(Calendar.YEAR)){
            if(transaction.getEndDate() != null && temp.compareTo(transaction.getEndDate()) > 0){
                return false;
            }
            if(temp.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH) && temp.get(Calendar.MONTH) == date.get(Calendar.MONTH) && temp.get(Calendar.YEAR) == date.get(Calendar.YEAR)){
                return true;
            }
            temp.add(Calendar.DATE, transaction.getTransactionInterval());
        }return false;
    }


    public void transactionsForCurrentDate(Calendar date){
        currentDateTransactions = new ArrayList<>();
        boolean uslov = true;
        for(TransactionModel t : interactor.getTransactions()){
            if(t.getType().equals(Type.REGULARINCOME) || t.getType().equals(Type.REGULARPAYMENT)){
                int broj = checkRegular(t, date);
                for(int i = 0; i < broj; i++) currentDateTransactions.add(t);
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
        } else if (temp[0].equals("Title ")) {
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
        for(TransactionModel tm : interactor.getTransactions()){
            if(tm.getType().equals(Type.REGULARPAYMENT) || tm.getType().equals(Type.REGULARINCOME)){
                int broj = checkRegular(tm, date);
                if(broj != 0){
                    if(tm.getType().equals(Type.REGULARPAYMENT)) {
                        for(int i = 0; i < broj; i++)
                            ret += tm.getAmount();
                    }else{
                        for(int i = 0; i < broj; i++)
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
        for(TransactionModel tm : interactor.getTransactions()){
            if(tm.getType().equals(Type.INDIVIDUALINCOME) || tm.getType().equals(Type.REGULARINCOME)){
                ret -= tm.getAmount();
            }else{
                ret += tm.getAmount();
            }
        }
        return ret;
    }

    public void subtractFromAccountBudget(double iznos) {
        account.updateBudget(iznos);
    }
    public AccountPresenter getAccount() {
        return ((AccountPresenter) account);
    }

    public void setAccount(AccountPresenter account) {
        this.account = account;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(((AccountPresenter) account), flags);
    }

    public double getIncomeForMonth(int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);

        double amount = 0;

        for(TransactionModel t : interactor.getTransactions()){
            if(t.getType().equals(Type.INDIVIDUALINCOME)){
                if(t.getDate().get(Calendar.MONTH) == month && t.getDate().get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                    amount += t.getAmount();
                }
            }else if(t.getType().equals(Type.REGULARINCOME)){
                int broj = checkRegular(t, calendar);
                for(int i = 0; i < broj; i++){
                    amount += t.getAmount();
                }
            }
        }return amount;
    }

    public double getSpendingForMonth(int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);

        double amount = 0;
        for(TransactionModel t : interactor.getTransactions()){
            if(t.getType().equals(Type.INDIVIDUALPAYMENT) || t.getType().equals(Type.PURCHASE)){
                if(t.getDate().get(Calendar.MONTH) == month && t.getDate().get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                    amount += t.getAmount();
                }
            }else if(t.getType().equals(Type.REGULARPAYMENT)){
                int broj = checkRegular(t, calendar);
                for(int i = 0; i < broj; i++){
                    amount += t.getAmount();
                }
            }
        }return amount;
    }

    public double getIncomeForWeek(int week){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.WEEK_OF_MONTH, week);

        double amount = 0;

        for(TransactionModel t : interactor.getTransactions()){
            if(t.getType().equals(Type.INDIVIDUALINCOME)){
                if(t.getDate().get(Calendar.WEEK_OF_MONTH) == week && t.getDate().get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && t.getDate().get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                    amount += t.getAmount();
                }
            }else if(t.getType().equals(Type.REGULARINCOME)){
                if(checkRegularWeek(t, calendar)){
                    amount += t.getAmount();
                }
            }
        }return amount;
    }

    public double getSpendingForWeek(int week){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.WEEK_OF_MONTH, week);

        double amount = 0;
        for(TransactionModel t : interactor.getTransactions()){
            if(t.getType().equals(Type.INDIVIDUALPAYMENT) || t.getType().equals(Type.PURCHASE)){
                if(t.getDate().get(Calendar.WEEK_OF_MONTH) == week && t.getDate().get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && t.getDate().get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                    amount += t.getAmount();
                }
            }else if(t.getType().equals(Type.REGULARPAYMENT)){
                if(checkRegularWeek(t, calendar)){
                    amount += t.getAmount();
                }
            }
        }return amount;
    }

    public double getIncomeForDay(int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);

        double amount = 0;

        for(TransactionModel t : interactor.getTransactions()){
            if(t.getType().equals(Type.INDIVIDUALINCOME)){
                if(t.getDate().get(Calendar.DAY_OF_MONTH) == day && t.getDate().get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && t.getDate().get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                    amount += t.getAmount();
                }
            }else if(t.getType().equals(Type.REGULARINCOME)){
                if(checkRegularDay(t, calendar)){
                    amount += t.getAmount();
                }
            }
        }return amount;
    }

    public double getSpendingForDay(int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);

        double amount = 0;
        for(TransactionModel t : interactor.getTransactions()){
            if(t.getType().equals(Type.INDIVIDUALPAYMENT) || t.getType().equals(Type.PURCHASE)){
                if(t.getDate().get(Calendar.DAY_OF_MONTH) == day && t.getDate().get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && t.getDate().get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                    amount += t.getAmount();
                }
            }else if(t.getType().equals(Type.REGULARPAYMENT)){
                if(checkRegularDay(t, calendar)){
                    amount += t.getAmount();
                }
            }
        }return amount;
    }

    @Override
    public void onDone(ArrayList<TransactionModel> result) {
        interactor.setTransactions(result);
        currentDateTransactions = new ArrayList<>(result);
        view.notifyTransactionsChanged();
    }
    public void getTransactions(String transactionTypeId, String sort, String month, String year){
        new TransactionInteractor((TransactionInteractor.OnTransactionSearchDone)this).execute(transactionTypeId, sort, month, year);
    }
    public void setTransactions(ArrayList<TransactionModel> transactions){
        interactor.setTransactions(transactions);
    }
}
