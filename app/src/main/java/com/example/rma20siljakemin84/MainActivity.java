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
                Bundle bundle = new Bundle();
                bundle.putString("global", ((TransactionListFragment)listFragment).getPresenter().getAccount().getBudget() + "");
                bundle.putString("limit", ((TransactionListFragment)listFragment).getPresenter().getAccount().getOverallLimit() + "");
                bundle.putString("month", ((TransactionListFragment)listFragment).getPresenter().getAccount().getMonthlyLimit() + "");
                bundle.putString("dodavanje", "da");
                detailFragment.setArguments(bundle);
                fm.beginTransaction().replace(R.id.transaction_details, detailFragment).commit();
            }
        }else{
            twoPaneMode = false;
        }


    }
}
