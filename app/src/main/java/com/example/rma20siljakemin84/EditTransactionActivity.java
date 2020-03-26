package com.example.rma20siljakemin84;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

    private EditText.OnFocusChangeListener dateListener =
            new EditText.OnFocusChangeListener(){

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String datum = ((EditText) v).getText().toString();
                    try {
                        Date temp = new SimpleDateFormat("dd.MM.yyyy").parse(datum);
                        if(v.getId() == R.id.endDate){
                            if(temp.compareTo(new SimpleDateFormat("dd.MM.yyyy").parse(date.getText().toString())) < 0){
                                throw new ParseException("poruka", 0);
                            }
                        }
                        ((EditText) v).setBackgroundColor(Color.GREEN);
                    } catch (ParseException e) {
                        ((EditText) v).setBackgroundColor(Color.RED);
                    }
                }
            };
    private EditText.OnFocusChangeListener emptyListener =
            new EditText.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(v != null && !((EditText) v).getText().toString().trim().isEmpty()){
                        ((EditText) v).setBackgroundColor(Color.GREEN);
                    }else{
                        ((EditText) v).setBackgroundColor(Color.RED);
                    }
                }
            };
    private EditText.OnFocusChangeListener titleListener =
            new EditText.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    int duzina = ((EditText) v).getText().toString().length();
                    if(duzina > 3 && duzina < 15){
                        ((EditText) v).setBackgroundColor(Color.GREEN);
                    }else{
                        ((EditText) v).setBackgroundColor(Color.RED);
                    }
                }
            };
    private Button.OnClickListener saveListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(saveTest()){
                        try {
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
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        transaction.setId(id);
                        presenter.updateTransaction(transaction);
                    }
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
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

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

        date.setOnFocusChangeListener(dateListener);
        endDate.setOnFocusChangeListener(dateListener);
        amount.setOnFocusChangeListener(emptyListener);
        interval.setOnFocusChangeListener(emptyListener);
        title.setOnFocusChangeListener(titleListener);
        description.setOnFocusChangeListener(emptyListener);
        saveBtn.setOnClickListener(saveListener);
        spinnerType.setOnItemSelectedListener(spinnerListener);

    }
    public TransactionModel getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionModel transaction) {
        this.transaction = transaction;
    }
}
