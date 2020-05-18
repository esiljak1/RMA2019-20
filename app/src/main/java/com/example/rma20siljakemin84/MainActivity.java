package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity implements ITransactionView, IAccountView{
    private boolean twoPaneMode = false;
    private boolean transactionsSet = false;
    
    private TransactionPresenter presenter;    //presenter drzimo u mainu da mu mozemo pristupati iz svih fragmenata

    public TransactionPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    public boolean isTwoPaneMode() {
        return twoPaneMode;
    }

    public void setTwoPaneMode(boolean twoPaneMode) {
        this.twoPaneMode = twoPaneMode;
    }

    public boolean isTransactionsSet() {
        return transactionsSet;
    }

    public void setTransactionsSet(boolean transactionsSet) {
        this.transactionsSet = transactionsSet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("RMA Spirala");


        FragmentManager fm = getSupportFragmentManager();

        Fragment listFragment = fm.findFragmentByTag("list");

        if(listFragment == null){
            listFragment = new TransactionListFragment();
            fm.beginTransaction().replace(R.id.transactions_list, listFragment, "list").commit();
        }else{
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FrameLayout detail = findViewById(R.id.transaction_details);

        if(detail != null){
            twoPaneMode = true;
            TransactionDetailFragment detailFragment = (TransactionDetailFragment) fm.findFragmentById(R.id.transaction_details);
            if(detailFragment == null){
                detailFragment = new TransactionDetailFragment();
                fm.beginTransaction().replace(R.id.transaction_details, detailFragment).commit();
            }
        }else{
            twoPaneMode = false;
        }


    }

    @Override
    public void notifyTransactionsChanged() {

    }

    @Override
    public void notifyAddedTransaction(TransactionModel transaction) {

    }

    @Override
    public void notifyAccountDetailsChanged() {

    }
}
