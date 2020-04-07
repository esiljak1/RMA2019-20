package com.example.rma20siljakemin84;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TransactionDetailFragment extends Fragment {
    private EditText date, amount, title, description, interval, endDate;
    private Button saveBtn, closeBtn, deleteBtn;
    private Spinner spinnerType;
    private TextView budgetEdit, limitEdit;
    private View view;

    private ArrayList<Type> typeList = new ArrayList<>();
    private ArrayAdapter<Type> typeListAdapter;
    private TransactionModel transaction;
    private TransactionPresenter presenter = new TransactionPresenter(new MainActivity());

    private int id = -1;
    private double budget;
    private double monthLimit;
    private double oldAmount = 0;

    private Button.OnClickListener closeListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    TransactionListFragment listFragment = new TransactionListFragment();
                    getFragmentManager().beginTransaction().replace(R.id.transactions_list, listFragment).addToBackStack(null).commit();
                }
            };
    private Button.OnClickListener saveListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(checkFields() && checkIfAmountNotOverBudget()){
                        checkIfOverLimitAndUpdateTransaction();
                    }else{
                        new AlertDialog.Builder(getContext()).setTitle("Wrong credentials").setMessage("Please fill in fields with correct data")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                    }
                }
            };
    private Button.OnClickListener deleteListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext()).setTitle("Deleting transaction").setMessage("Are you sure you want to delete this transaction?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    transaction = new TransactionModel();
                                    transaction.setId(id);      //postavljamo id da bi se trazena transakcija izbrisala iz liste
                                    presenter.deleteTransaction(transaction);
                                    TransactionListFragment listFragment = new TransactionListFragment();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, listFragment).addToBackStack(null).commit();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
            };
    private Spinner.OnItemSelectedListener spinnerListener =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0 || position == 2 || position == 4){
                        endDate.setEnabled(false);
                        interval.setEnabled(false);
                    }else{
                        endDate.setEnabled(true);
                        interval.setEnabled(true);
                    }
                    if(position == 3 || position == 4){
                        description.setEnabled(false);
                    }else{
                        description.setEnabled(true);
                    }
                    if(id != -1){
                        try{
                            ((ColorDrawable) spinnerType.getBackground()).getColor();
                        } catch (Exception ex){
                            spinnerType.setBackgroundColor(Color.TRANSPARENT);
                            return;
                        }
                    }spinnerType.setBackgroundColor(Color.GREEN);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    private void changeTransaction() throws ParseException {
        Date temp = new SimpleDateFormat("dd.MM.yyyy").parse(date.getText().toString());
        Calendar cal = Calendar.getInstance(), end = null;
        cal.set(Calendar.DAY_OF_MONTH, temp.getDate());
        cal.set(Calendar.MONTH, temp.getMonth());
        cal.set(Calendar.YEAR, temp.getYear() + 1900);      //iz nekog razloga formatiranje datuma daje godinu koja je za 1900 manja od prave godine
        if(endDate.isEnabled()){
            temp = new SimpleDateFormat("dd.MM.yyyy").parse(endDate.getText().toString());
            end = Calendar.getInstance();
            end.set(Calendar.DAY_OF_MONTH, temp.getDay());
            end.set(Calendar.MONTH, temp.getMonth());
            end.set(Calendar.YEAR, temp.getYear() + 1900);
        }
        transaction = new TransactionModel(cal, Double.parseDouble(amount.getText().toString()), title.getText().toString(), (Type) spinnerType.getSelectedItem(),
                description.isEnabled() ? description.getText().toString() : null,
                interval.isEnabled() ? Integer.parseInt(interval.getText().toString()) : 0, end);
        if(id != -1) {
            transaction.setId(id);
        }else{
            id = transaction.getId();       //postavljamo id ukoliko dodajemo novu transakciju da bismo odmah mogli i update/delete po potrebi tu transakciju
        }

        presenter.updateTransaction(transaction);

        if(getActivity().findViewById(R.id.transaction_details) != null){
            TransactionListFragment listFragment = new TransactionListFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, listFragment).addToBackStack(null).commit();
        }

        date.setBackgroundColor(Color.TRANSPARENT);
        amount.setBackgroundColor(Color.TRANSPARENT);
        title.setBackgroundColor(Color.TRANSPARENT);
        spinnerType.setBackgroundColor(Color.TRANSPARENT);
        if(description.isEnabled()) description.setBackgroundColor(Color.TRANSPARENT);
        if(interval.isEnabled()) interval.setBackgroundColor(Color.TRANSPARENT);
        if(endDate.isEnabled()) interval.setBackgroundColor(Color.TRANSPARENT);
        if(!deleteBtn.isEnabled()) deleteBtn.setEnabled(true);      //ako dodajemo transakciju delete je disabled, cim dodamo transakciju enable-ujemo delete jer tada editujemo transakciju
    }
    private boolean checkIfAmountNotOverBudget(){
        Type temp = (Type) spinnerType.getSelectedItem();
        if(temp.equals(Type.REGULARINCOME) || temp.equals(Type.INDIVIDUALINCOME)){      //ako je income dodajemo na budzet pa ne mozemo potrositi budzet
            return true;
        }
        double iznos = Double.parseDouble(amount.getText().toString()) - oldAmount;
        return iznos <= budget;
    }

    private void checkIfOverLimitAndUpdateTransaction(){
        final double iznos = Double.parseDouble(amount.getText().toString()) - oldAmount, ukupno = Double.parseDouble(limitEdit.getText().toString());
        Calendar temp = Calendar.getInstance();
        Date d = null;
        try {
            d = new SimpleDateFormat("dd.MM.yyyy").parse(date.getText().toString());
            temp.set(Calendar.DATE, d.getDate());
            temp.set(Calendar.MONTH, d.getMonth());
            temp.set(Calendar.YEAR, d.getYear()+ 1900);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(iznos + presenter.getAmountforDate(temp) > monthLimit || iznos + presenter.getAllAmounts() > ukupno){
            new AlertDialog.Builder(getContext()).setTitle("Over the limit").setMessage("Are you sure you want to do this?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                changeTransaction();
                                int znak = 1;
                                if(spinnerType.getSelectedItem().equals(Type.REGULARINCOME) || spinnerType.getSelectedItem().equals(Type.INDIVIDUALINCOME)){        //ako je dobit postavljamo znak na -1
                                    znak = -1;                                                                                                                  //jer minus puta minus daju plus
                                }
                                presenter.subtractFromAccountBudget(iznos*znak);
                                budgetEdit.setText(presenter.getAccount().getBudget() + "");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }else{
            try {
                changeTransaction();
                int znak = 1;
                if(spinnerType.getSelectedItem().equals(Type.REGULARINCOME) || spinnerType.getSelectedItem().equals(Type.INDIVIDUALINCOME)){
                    znak = -1;
                }
                presenter.subtractFromAccountBudget(iznos*znak);
                budgetEdit.setText(presenter.getAccount().getBudget() + "");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private void updateInAnotherFragment(){
        TransactionListFragment fragment = (TransactionListFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.transactions_list);
        fragment.getPresenter().setCurrentDateTransactions(presenter.getCurrentDateTransactions());
    }
    private boolean checkIfFieldIsNotRed(EditText text){
        try{
            if(text.isEnabled()) {
                if (((ColorDrawable) text.getBackground()).getColor() == Color.RED)
                    return false;
            }
        }catch(Exception ex){
            return true;
        }return true;
    }

    private boolean checkFields(){
        return checkIfFieldIsNotRed(date) && checkIfFieldIsNotRed(amount) && checkIfFieldIsNotRed(title) && checkIfFieldIsNotRed(description) && checkIfFieldIsNotRed(interval) && checkIfFieldIsNotRed(endDate);
    }

    private void napuniSpinner(){
        typeList.add(Type.INDIVIDUALPAYMENT);
        typeList.add(Type.REGULARPAYMENT);
        typeList.add(Type.PURCHASE);
        typeList.add(Type.REGULARINCOME);
        typeList.add(Type.INDIVIDUALINCOME);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) view = inflater.inflate(R.layout.fragment_transaction_detail, container, false);

        napuniSpinner();
        typeListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typeList);

        date = (EditText) view.findViewById(R.id.date);
        amount = (EditText) view.findViewById(R.id.amount);
        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        interval = (EditText) view.findViewById(R.id.interval);
        endDate = (EditText) view.findViewById(R.id.endDate);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        closeBtn = (Button) view.findViewById(R.id.closeBtn);
        deleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        spinnerType = (Spinner) view.findViewById(R.id.spinnerType);
        spinnerType.setAdapter(typeListAdapter);
        budgetEdit = (TextView) view.findViewById(R.id.globalEdit);
        limitEdit = (TextView) view.findViewById(R.id.limitEdit);

        Bundle arguments = getArguments();

        spinnerType.setOnItemSelectedListener(spinnerListener);

        if(arguments != null && arguments.getString("dodavanje") == null) {
            date.setText(arguments.getString("date"));
            amount.setText(arguments.getString("amount"));
            oldAmount = Double.parseDouble(amount.getText().toString());
            title.setText(arguments.getString("title"));
            spinnerType.setSelection(typeList.indexOf(arguments.getSerializable("type")));
            if (arguments.getString("description") != null) {
                description.setText(arguments.getString("description"));
            } else {
                description.setEnabled(false);
            }
            if (arguments.getString("interval") != null && !arguments.getString("interval").equals("0")) {
                interval.setText(arguments.getString("interval"));
            } else {
                interval.setEnabled(false);
            }
            if (arguments.getString("endDate") != null) {
                endDate.setText(arguments.getString("endDate"));
            } else {
                endDate.setEnabled(false);
            }
            id = Integer.parseInt(arguments.getString("id"));
        }else{
            spinnerType.setBackgroundColor(Color.GREEN);
            spinnerType.setSelection(0);
            date.setBackgroundColor(Color.RED);
            amount.setBackgroundColor(Color.RED);
            title.setBackgroundColor(Color.GREEN);
            description.setBackgroundColor(Color.GREEN);
            deleteBtn.setEnabled(false);
        }
        if(arguments != null && arguments.getString("global") != null) {
            budgetEdit.setText(arguments.getString("global"));
            limitEdit.setText(arguments.getString("limit"));
            budget = Double.parseDouble(arguments.getString("global"));
            monthLimit = Double.parseDouble(arguments.getString("month"));
        }else{
            interval.setEnabled(false);
            endDate.setEnabled(false);
            budget = ((AccountPresenter) arguments.getParcelable("account")).getBudget();
            monthLimit = ((AccountPresenter) arguments.getParcelable("account")).getOverallLimit();
            budgetEdit.setText( budget + "");
            limitEdit.setText( monthLimit+ "");
        }

        saveBtn.setOnClickListener(saveListener);

        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!s.toString().trim().isEmpty()){
                    try {
                        new SimpleDateFormat("dd.MM.yyyy").parse(s.toString());
                    } catch (ParseException e) {
                        date.setBackgroundColor(Color.RED);
                        return;
                    }
                    date.setBackgroundColor(Color.GREEN);
                }else{
                    date.setBackgroundColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        endDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    try {
                        Date end = new SimpleDateFormat("dd.MM.yyyy").parse(s.toString()), temp = new SimpleDateFormat("dd.MM.yyyy").parse(date.getText().toString());
                        if(end.compareTo(temp) < 0){
                            throw new ParseException("Samo zbog mijenjanja boje", 0);
                        }
                    } catch (ParseException e) {
                        endDate.setBackgroundColor(Color.RED);
                        return;
                    }
                    endDate.setBackgroundColor(Color.GREEN);
                }else{
                    endDate.setBackgroundColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    amount.setBackgroundColor(Color.GREEN);
                }else{
                    amount.setBackgroundColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        interval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    interval.setBackgroundColor(Color.GREEN);
                }else{
                    interval.setBackgroundColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 3 && s.toString().length() < 15){
                    title.setBackgroundColor(Color.GREEN);
                }else{
                    title.setBackgroundColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    description.setBackgroundColor(Color.GREEN);
                }else{
                    description.setBackgroundColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        deleteBtn.setOnClickListener(deleteListener);
        closeBtn.setOnClickListener(closeListener);

        return view;
    }
}
