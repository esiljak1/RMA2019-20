package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class AccountDetailsFragment extends Fragment {
    private TextView budgetAccountDetails;
    private EditText globalLimitAccountDetails, monthLimitAccountDetails;
    private Button saveBtnAccountDetails;

    private View view;
    private AccountPresenter account;
    private double oldTouchValue = 0;

    private Button.OnClickListener saveListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    account.setOverallLimit(Double.parseDouble(globalLimitAccountDetails.getText().toString()));
                    account.setMonthlyLimit(Double.parseDouble(monthLimitAccountDetails.getText().toString()));
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account_details, container, false);

        budgetAccountDetails = view.findViewById(R.id.budgetAccountDetails);
        globalLimitAccountDetails = view.findViewById(R.id.globalLimitAccountDetails);
        monthLimitAccountDetails = view.findViewById(R.id.monthLimitAccountDetails);
        saveBtnAccountDetails = view.findViewById(R.id.saveBtnAccountDetails);

        Bundle bundle = getArguments();

        account = bundle.getParcelable("account");
        budgetAccountDetails.setText(account.getBudget() + "");
        globalLimitAccountDetails.setText(account.getOverallLimit() + "");
        monthLimitAccountDetails.setText(account.getMonthlyLimit() + "");

        saveBtnAccountDetails.setOnClickListener(saveListener);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                    {
                        oldTouchValue = event.getX();
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        double current = event.getX();
                        if(oldTouchValue < current){
                            TransactionListFragment listFragment = new TransactionListFragment();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, listFragment).addToBackStack(null).commit();
                        }
                        if(oldTouchValue > current){
                            System.out.println("Desni swipe");
                        }
                    }
                }
                return false;
            }
        });

        return view;
    }
}