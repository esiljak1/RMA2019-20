package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private List<Transaction> transactions = new ArrayList<>();
    private TransactionListAdapter adapter;
    private List<String> sorts = new ArrayList<>();
    private List<Type> filters = new ArrayList<>();
    private ArrayAdapter spinSortAdapter;
    private TypeListAdapter spinFilterAdapter;
    private Date date = new Date(2020, 2, 21);

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
        transactions = Transaction.napuni();
        setSorts();
        setFilters();

        spinSortAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sorts);
        spinnerSort.setAdapter(spinSortAdapter);

        spinFilterAdapter = new TypeListAdapter(this, R.layout.type_element, filters);
        spinnerFilter.setAdapter(spinFilterAdapter);

        adapter = new TransactionListAdapter(this, R.layout.list_element, transactions);
        listView.setAdapter(adapter);

        date.setYear(date.getYear() - 1900);
        SimpleDateFormat format = new SimpleDateFormat("MMMM, yyyy");
        textDate.setText(format.format(date));
    }
}
