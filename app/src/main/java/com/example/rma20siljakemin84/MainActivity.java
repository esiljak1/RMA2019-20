package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Spinner spinnerSort, spinnerFilter;
    private TextView textView, textView2, textView3, textView4, textView5;
    private Button button;

    private List<Transaction> transactions = new ArrayList<>();
    private TransactionListAdapter adapter;
    private List<String> sorts = new ArrayList<>();
    private ArrayAdapter spinAdapter;

    private void setSorts(){
        sorts.add("Price - Ascending");
        sorts.add("Price - Descending");
        sorts.add("Title - Ascending");
        sorts.add("Title - Descending");
        sorts.add("Date - Ascending");
        sorts.add("Date - Descending");
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
        textView5 = (TextView)findViewById(R.id.textView5);
        button = (Button)findViewById(R.id.button);
        transactions = Transaction.napuni();
        setSorts();

        spinAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sorts);
        spinnerSort.setAdapter(spinAdapter);

        adapter = new TransactionListAdapter(this, R.layout.list_element, transactions);
        listView.setAdapter(adapter);

    }
}
