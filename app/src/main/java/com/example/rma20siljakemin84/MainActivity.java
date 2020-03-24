package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Spinner spinnerSort, spinnerFilter;
    private TextView textView, textView2, textView3, textView4, textDate;
    private Button button;
    private ImageButton leftBtn, rightBtn;

    private List<Transaction> transactions = new ArrayList<>();
    private TransactionListAdapter adapter;
    private List<String> sorts = new ArrayList<>();
    private List<Type> filters = new ArrayList<>();
    private ArrayAdapter spinSortAdapter;
    private TypeListAdapter spinFilterAdapter;
    private Date date = new Date(2020, 2, 21);
    private SimpleDateFormat format = new SimpleDateFormat("MMMM, yyyy");

    private ImageButton.OnClickListener listenerLeft =
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {
                    date.setMonth(date.getMonth() - 1);
                    textDate.setText(format.format(date));
                    filter();
                }
            };
    private ImageButton.OnClickListener listenerRight =
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {
                    date.setMonth(date.getMonth() + 1);
                    textDate.setText(format.format(date));
                    filter();
                }
            };

    private void setSorts(){
        sorts.add("Price - Ascending");
        sorts.add("Price - Descending");
        sorts.add("Title - Ascending");
        sorts.add("Title - Descending");
        sorts.add("Date - Ascending");
        sorts.add("Date - Descending");
    }
    private void setFilters(){
        filters.add(Type.INDIVIDUALPAYMENT);
        filters.add(Type.REGULARPAYMENT);
        filters.add(Type.PURCHASE);
        filters.add(Type.INDIVIDUALINCOME);
        filters.add(Type.REGULARINCOME);
    }
    private void filter(){
        transactions = Transaction.napuni();
        ArrayList<Transaction> temp = new ArrayList<>();
        for(Transaction t : transactions){
            if(t.getDate().getMonth() == date.getMonth() && t.getDate().getYear() - 1900 == date.getYear()){
                temp.add(t);
            }
        }transactions = temp;
        adapter = new TransactionListAdapter(this, R.layout.list_element, transactions);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listView);
        spinnerSort = (Spinner)findViewById(R.id.spinnerSort);
        spinnerFilter = (Spinner)findViewById(R.id.spinnerFilter);
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textDate = (TextView)findViewById(R.id.textDate);
        button = (Button)findViewById(R.id.button);
        leftBtn = (ImageButton)findViewById(R.id.leftBtn);
        rightBtn = (ImageButton)findViewById(R.id.rightBtn);
        transactions = Transaction.napuni();
        date.setYear(date.getYear() - 1900);
        textDate.setText(format.format(date));
        filter();
        setSorts();
        setFilters();

        spinSortAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sorts);
        spinnerSort.setAdapter(spinSortAdapter);

        spinFilterAdapter = new TypeListAdapter(this, R.layout.type_element, filters);
        spinnerFilter.setAdapter(spinFilterAdapter);

        adapter = new TransactionListAdapter(this, R.layout.list_element, transactions);
        listView.setAdapter(adapter);

        leftBtn.setOnClickListener(listenerLeft);
        rightBtn.setOnClickListener(listenerRight);

    }
}
