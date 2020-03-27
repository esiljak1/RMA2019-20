package com.example.rma20siljakemin84;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditTransactionActivity extends AppCompatActivity {
    private EditText date, amount, title, description, interval, endDate;
    private Button saveBtn, closeBtn, deleteBtn;
    private Spinner spinnerType;
    private TextView globalEdit, monthEdit;

    private ArrayList<Type> list = new ArrayList<>();
    private ArrayAdapter<Type> adapter;

    private TransactionModel transaction;
    private TransactionPresenter presenter = new TransactionPresenter(new MainActivity());
    private int id;

    private double oldAmount = 0;

    private Button.OnClickListener saveListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(saveTest()){
                        checkIfOver();
                    }else{
                        new AlertDialog.Builder(EditTransactionActivity.this).setTitle("Wrong credentials").setMessage("Please fill in fields with correct data").show();
                    }
                }
            };
    private Button.OnClickListener deleteListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(EditTransactionActivity.this).setTitle("Deleting transaction").setMessage("Are you sure you want to delete this transaction?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    transaction = new TransactionModel();
                                    transaction.setId(id);
                                    presenter.deleteTransaction(transaction);
                                    EditTransactionActivity.super.onBackPressed();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                    }).show();
                }
            };

    private void changeTransaction() throws ParseException {
        Date temp = new SimpleDateFormat("dd.MM.yyyy").parse(date.getText().toString());
        Calendar cal = Calendar.getInstance(), end = null;
        cal.set(Calendar.DAY_OF_MONTH, temp.getDate());
        cal.set(Calendar.MONTH, temp.getMonth());
        cal.set(Calendar.YEAR, temp.getYear() + 1900);
        if(endDate.isEnabled()){
            temp = new SimpleDateFormat("dd.MM.yyyy").parse(endDate.getText().toString());
            end.set(Calendar.DAY_OF_MONTH, temp.getDay());
            end.set(Calendar.MONTH, temp.getMonth());
            end.set(Calendar.YEAR, temp.getYear() + 1900);
        }
        transaction = new TransactionModel(cal, Double.parseDouble(amount.getText().toString()), title.getText().toString(), (Type) spinnerType.getSelectedItem(),
                description.isEnabled() ? description.getText().toString() : null,
                interval.isEnabled() ? Integer.parseInt(interval.getText().toString()) : 0, end);
        transaction.setId(id);
        presenter.updateTransaction(transaction);
        date.setBackgroundColor(Color.TRANSPARENT);
        amount.setBackgroundColor(Color.TRANSPARENT);
        title.setBackgroundColor(Color.TRANSPARENT);
        spinnerType.setBackgroundColor(Color.TRANSPARENT);
        if(description.isEnabled()) description.setBackgroundColor(Color.TRANSPARENT);
        if(interval.isEnabled()) interval.setBackgroundColor(Color.TRANSPARENT);
        if(endDate.isEnabled()) interval.setBackgroundColor(Color.TRANSPARENT);
    }

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
                    spinnerType.setBackgroundColor(Color.GREEN);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    private void checkIfOver(){
        final double iznos = Double.parseDouble(amount.getText().toString()) - oldAmount, mjesecno = Double.parseDouble(monthEdit.getText().toString()),
                    ukupno = Double.parseDouble(globalEdit.getText().toString());
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
        if(iznos + presenter.getAmountforDate(temp) > mjesecno || iznos + presenter.getAllAmounts() > ukupno){
            new AlertDialog.Builder(this).setTitle("Over the limit").setMessage("Are you sure you want to do this?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                changeTransaction();
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
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean exceptionTest(EditText text){
        try{
            if(text.isEnabled()) {
                if (((ColorDrawable) text.getBackground()).getColor() == Color.RED)
                    return false;
            }
        }catch(Exception ex){
            return true;
        }return true;
    }

    private boolean saveTest(){
         return exceptionTest(date) && exceptionTest(amount) && exceptionTest(title) && exceptionTest(description) && exceptionTest(interval) && exceptionTest(endDate);
    }

    private void napuniSpinner(){
        list.add(Type.INDIVIDUALPAYMENT);
        list.add(Type.REGULARPAYMENT);
        list.add(Type.PURCHASE);
        list.add(Type.REGULARINCOME);
        list.add(Type.INDIVIDUALINCOME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        setTitle("Edit transaction");

        napuniSpinner();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);

        date = (EditText) findViewById(R.id.date);
        amount = (EditText) findViewById(R.id.amount);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        interval = (EditText) findViewById(R.id.interval);
        endDate = (EditText) findViewById(R.id.endDate);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        closeBtn = (Button) findViewById(R.id.closeBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        spinnerType.setAdapter(adapter);
        globalEdit = (TextView) findViewById(R.id.globalEdit);
        monthEdit = (TextView) findViewById(R.id.monthEdit);

        Intent receivedIntent = getIntent();

        date.setText(receivedIntent.getStringExtra("date"));
        amount.setText(receivedIntent.getStringExtra("amount"));
        oldAmount = Double.parseDouble(amount.getText().toString());
        title.setText(receivedIntent.getStringExtra("title"));
        spinnerType.setSelection(list.indexOf(receivedIntent.getSerializableExtra("type")));
        if(receivedIntent.getStringExtra("description") != null){
            description.setText(receivedIntent.getStringExtra("description"));
        }else{
            description.setEnabled(false);
        }
        if(receivedIntent.getStringExtra("interval") != null && !receivedIntent.getStringExtra("interval").equals("0")){
            interval.setText(receivedIntent.getStringExtra("interval"));
        }else{
            interval.setEnabled(false);
        }
        if(receivedIntent.getStringExtra("endDate") != null){
            endDate.setText(receivedIntent.getStringExtra("endDate"));
        }else{
            endDate.setEnabled(false);
        }
        globalEdit.setText(receivedIntent.getStringExtra("global"));
        monthEdit.setText(receivedIntent.getStringExtra("month"));
        id = Integer.parseInt(receivedIntent.getStringExtra("id"));

        saveBtn.setOnClickListener(saveListener);
        spinnerType.setOnItemSelectedListener(spinnerListener);

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

    }
    public TransactionModel getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionModel transaction) {
        this.transaction = transaction;
    }
}
