package com.example.rma20siljakemin84;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class AccountDetailsFragment extends Fragment implements IAccountView{
    private static final int MIN_DISTANCE = 800;        //pomocna varijabla kod swipe-a ukoliko nema ovog i najmanji pokret dovodi do swipe-a

    private TextView budgetAccountDetails, onlineAccountText;
    private EditText globalLimitAccountDetails, monthLimitAccountDetails;
    private Button saveBtnAccountDetails;

    private View view;
    private AccountPresenter account;
    private double oldTouchValue = 0;

    private Button.OnClickListener saveListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    try {
                        double budget = account.getBudget();
                        double totalLimit = Double.parseDouble(globalLimitAccountDetails.getText().toString());
                        double monthLimit = Double.parseDouble(monthLimitAccountDetails.getText().toString());
                        ((MainActivity) getActivity()).getPresenter().getAccount().updateAccount(budget, totalLimit, monthLimit, ((MainActivity) getActivity()).isConnectedToTheInternet(), getContext());
                        account.setOverallLimit(Double.parseDouble(globalLimitAccountDetails.getText().toString()));
                        account.setMonthlyLimit(Double.parseDouble(monthLimitAccountDetails.getText().toString()));
                    } catch (IllegalAmountException e) {
                        e.printStackTrace();
                        errorScreen();
                    }
                }
            };
    private View.OnTouchListener swipeListener =
            new View.OnTouchListener(){

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
                            double deltaX = oldTouchValue - event.getX();
                            if(Math.abs(deltaX) > MIN_DISTANCE) {
                                if (deltaX < 0) {
                                    if (getActivity().findViewById(R.id.transaction_details) != null) {
                                        return false;
                                    }
                                    TransactionListFragment listFragment = new TransactionListFragment();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, listFragment).commit();
                                }
                                if (deltaX > 0) {
                                    if (getActivity().findViewById(R.id.transaction_details) != null) {
                                        return false;
                                    }
                                    GraphsFragment graphsFragment = new GraphsFragment();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, graphsFragment).commit();
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account_details, container, false);

        ((MainActivity) getActivity()).getPresenter().getAccount().setView(this);

        budgetAccountDetails = view.findViewById(R.id.budgetAccountDetails);
        globalLimitAccountDetails = view.findViewById(R.id.globalLimitAccountDetails);
        monthLimitAccountDetails = view.findViewById(R.id.monthLimitAccountDetails);
        saveBtnAccountDetails = view.findViewById(R.id.saveBtnAccountDetails);
        onlineAccountText = view.findViewById(R.id.onlineAccountText);

        account = ((MainActivity) getActivity()).getPresenter().getAccount();
        budgetAccountDetails.setText(String.format("%.2f", account.getBudget()));
        globalLimitAccountDetails.setText(account.getOverallLimit() + "");
        monthLimitAccountDetails.setText(account.getMonthlyLimit() + "");

        if(((MainActivity) getActivity()).isConnectedToTheInternet()){
            onlineAccountText.setText("");
        }else{
            showOfflineAlert();
            onlineAccountText.setText("Offline editing");
        }

        saveBtnAccountDetails.setOnClickListener(saveListener);
        view.setOnTouchListener(swipeListener);

        return view;
    }

    @Override
    public void notifyAccountDetailsChanged() {
        try {
            account.setBudget(((MainActivity) getActivity()).getPresenter().getAccount().getBudget());
            account.setOverallLimit(((MainActivity) getActivity()).getPresenter().getAccount().getOverallLimit());
            account.setMonthlyLimit(((MainActivity) getActivity()).getPresenter().getAccount().getMonthlyLimit());
        } catch (IllegalAmountException e) {
            e.printStackTrace();
        }
    }

    public void errorScreen(){
        new AlertDialog.Builder(getContext()).setTitle("Illegal limit").setMessage("Cannot set limit amount to negative value").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private void showOfflineAlert(){
        new AlertDialog.Builder(getContext()).setTitle("Offline mode").setMessage("You are currently browsing in offline mode")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
}
