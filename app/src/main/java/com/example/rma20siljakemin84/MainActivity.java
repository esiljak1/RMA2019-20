package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                    getTransactionsForCurrentDate();
                }
            };
    private ImageButton.OnClickListener listenerRight =
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {
                    date.setMonth(date.getMonth() + 1);
                    textDate.setText(format.format(date));
                    getTransactionsForCurrentDate();
                }
            };
    private Spinner.OnItemSelectedListener listenerSort =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sort(sorts.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
    private Spinner.OnItemSelectedListener listenerFilter =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    filter(filters.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
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
    private void getTransactionsForCurrentDate(){
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
    private void sort(String s){
        String[] temp = s.split("-");
        Comparator<Transaction> comparator;
        if(temp[0].equals("Price ")){
            if(temp[1].equals(" Ascending")){
                comparator = new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return Double.compare(o1.getAmount(), o2.getAmount());
                    }
                };
            }else{
                comparator = new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return Double.compare(o2.getAmount(), o1.getAmount());
                    }
                };
            }Collections.sort(transactions, comparator);
        }else if(temp[0].equals("Title")){
            if(temp[1].equals(" Ascending")){
                comparator = new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                };
            }else{
                comparator = new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return o2.getTitle().compareTo(o1.getTitle());
                    }
                };

            }Collections.sort(transactions, comparator);
        }else{
            if(temp[1].equals(" Ascending")){
                comparator = new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                };
            }else{
                comparator = new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                };
            }Collections.sort(transactions, comparator);
        }adapter = new TransactionListAdapter(this, R.layout.list_element, transactions);
        listView.setAdapter(adapter);
    }
    private void filter(Type type){
        getTransactionsForCurrentDate();
        ArrayList<Transaction> temp = new ArrayList<>();
        for(Transaction t : transactions){
            if(t.getType().equals(type)){
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
        setTitle("RMA Spirala");

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
        getTransactionsForCurrentDate();
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

        //spinnerFilter.setSelection(spinFilterAdapter.getCount());
        spinnerSort.setOnItemSelectedListener(listenerSort);
        spinnerFilter.setOnItemSelectedListener(listenerFilter);
        sort(sorts.get(0));

    }
}
