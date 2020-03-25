package com.example.rma20siljakemin84;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditTransactionActivity extends AppCompatActivity {
    private EditText date, amount, title, description, interval, endDate;
    private Button saveBtn, cancelBtn, deleteBtn;
    private Spinner spinnerType;
    private TextView globalEdit, monthEdit;

    private ArrayList<Type> list = new ArrayList<>();
    private ArrayAdapter<Type> adapter;

    private EditText.OnFocusChangeListener listener =
            new EditText.OnFocusChangeListener(){

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        ((EditText) v).getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                    }else{
                        ((EditText) v).getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    }
                }
            };

    private void napuniSpinner(){
        list.add(Type.INDIVIDUALPAYMENT);
        list.add(Type.INDIVIDUALINCOME.REGULARPAYMENT);
        list.add(Type.PURCHASE);
        list.add(Type.INDIVIDUALINCOME);
        list.add(Type.REGULARINCOME);
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
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
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

        date.setOnFocusChangeListener(listener);

    }
}
