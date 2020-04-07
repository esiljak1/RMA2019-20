package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class AccountDetailsFragment extends Fragment {
    private TextView budgetAccountDetails;
    private EditText globalLimitAccountDetails, monthLimitAccountDetails;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account_details, container, false);

        budgetAccountDetails = view.findViewById(R.id.budgetAccountDetails);
        globalLimitAccountDetails = view.findViewById(R.id.globalLimitAccountDetails);
        monthLimitAccountDetails = view.findViewById(R.id.monthLimitAccountDetails);

        Bundle bundle = getArguments();

        budgetAccountDetails.setText(bundle.getString("budzet"));
        globalLimitAccountDetails.setText(bundle.getString("global"));
        monthLimitAccountDetails.setText(bundle.getString("month"));

        return view;
    }
}
