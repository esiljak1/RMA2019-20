package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity implements ITransactionView{
    private boolean twoPaneMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("RMA Spirala");

        FragmentManager fm = getSupportFragmentManager();
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

        Fragment listFragment = fm.findFragmentByTag("list");

        if(listFragment == null){
            listFragment = new TransactionListFragment();
            fm.beginTransaction().replace(R.id.transactions_list, listFragment, "list").commit();
        }else{
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }


    }

//    @Override
//    protected void onResume() {
//        spinnerFilter.setSelection(0);
//        spinnerSort.setSelection(0);
//        presenter.transactionsForCurrentDate(date);
//        transactionsAdapter = new TransactionListAdapter(this, R.layout.list_element, presenter.getCurrentDateTransactions());
//        listViewTransactions.setAdapter(transactionsAdapter);
//        textBudget.setText(presenter.getAccount().getBudget() + "");
//        super.onResume();
//    }
}
