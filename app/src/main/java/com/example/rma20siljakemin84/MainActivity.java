package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity implements ITransactionView{
    private boolean twoPaneMode = false;
    private TransactionPresenter presenter = new TransactionPresenter(this);

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
    public void funkcija(){

    }
}
