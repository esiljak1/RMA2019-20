package com.example.rma20siljakemin84;

import android.content.Intent;
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
import java.util.Date;
import java.util.List;

//Sve podatke o transakcijama u model sa svim geterima i seterima
//U presenteru imamo instancu modela i view-a
//U view imamo instancu presentera

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Spinner spinnerSort, spinnerFilter;
    private TextView textView, textView2, textView3, textView4, textDate;
    private Button button;
    private ImageButton leftBtn, rightBtn;

    private Account account = new Account();
    private ArrayList<TransactionModel> transactionModel = new ArrayList<>();
    private TransactionListAdapter adapter;
    private List<String> sorts = new ArrayList<>();
    private List<Type> filters = new ArrayList<>();
    private ArrayAdapter spinSortAdapter;
    private TypeListAdapter spinFilterAdapter;
    private Date date = new Date(2020, 2, 21);
    private SimpleDateFormat format = new SimpleDateFormat("MMMM, yyyy");

    private ArrayList<TransactionModel> availableTransactionModels = new ArrayList<>();

    private ImageButton.OnClickListener listenerLeft =
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {
                    date.setMonth(date.getMonth() - 1);
                    textDate.setText(format.format(date));
                    transactionModel = TransactionPresenter.getTransactionsForCurrentDate(date, availableTransactionModels);
                    TransactionPresenter.sort(transactionModel, (String) spinnerSort.getSelectedItem());
                    transactionModel = TransactionPresenter.filter(availableTransactionModels, date, (Type)spinnerFilter.getSelectedItem());
                    adapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, transactionModel);
                    listView.setAdapter(adapter);
                }
            };
    private ImageButton.OnClickListener listenerRight =
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {
                    date.setMonth(date.getMonth() + 1);
                    textDate.setText(format.format(date));
                    transactionModel = TransactionPresenter.getTransactionsForCurrentDate(date, availableTransactionModels);
                    TransactionPresenter.sort(transactionModel, (String) spinnerSort.getSelectedItem());
                    transactionModel = TransactionPresenter.filter(availableTransactionModels, date, (Type)spinnerFilter.getSelectedItem());
                    adapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, transactionModel);
                    listView.setAdapter(adapter);
                }
            };
    private Spinner.OnItemSelectedListener listenerSort =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TransactionPresenter.sort(transactionModel, sorts.get(position));
                    adapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, transactionModel);
                    listView.setAdapter(adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
    private Spinner.OnItemSelectedListener listenerFilter =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    transactionModel = TransactionPresenter.filter(availableTransactionModels, date, filters.get(position));
                    adapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, transactionModel);
                    listView.setAdapter(adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
    private ListView.OnItemClickListener itemClickListener =
            new ListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent editTransactionIntent = new Intent(MainActivity.this, EditTransactionActivity.class);
                    TransactionModel t = adapter.getItem(position);
                    Date temp = new Date(t.getDate().getYear() - 1900, t.getDate().getMonth(), t.getDate().getDay());
                    editTransactionIntent.putExtra("date", new SimpleDateFormat("dd.MM.yyyy").format(temp));
                    editTransactionIntent.putExtra("amount", t.getAmount() + "");
                    editTransactionIntent.putExtra("title", t.getTitle());
                    editTransactionIntent.putExtra("type", t.getType());
                    editTransactionIntent.putExtra("description", t.getItemDescription());
                    editTransactionIntent.putExtra("interval", t.getTransactionInterval() + "");
                    editTransactionIntent.putExtra("global", account.getTotalLimit() + "");
                    editTransactionIntent.putExtra("month", account.getMonthLimit() + "");
                    if(t.getEndDate() != null) {
                        temp = new Date(t.getEndDate().getYear() - 1900, t.getEndDate().getMonth(), t.getEndDate().getDay());
                        editTransactionIntent.putExtra("endDate", new SimpleDateFormat("dd.MM.yyyy").format(temp));
                    }
                    MainActivity.this.startActivity(editTransactionIntent);
                }
            };

    private void setSorts(){
        sorts.add("Sort by");
        sorts.add("Price - Ascending");
        sorts.add("Price - Descending");
        sorts.add("Title - Ascending");
        sorts.add("Title - Descending");
        sorts.add("Date - Ascending");
        sorts.add("Date - Descending");
    }
    private void setFilters(){
        filters.add(Type.Dummy);
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
        setTitle("RMA Spirala");

        account.setMonthLimit(2000);
        account.setTotalLimit(10000);
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
        transactionModel = TransactionPresenter.getTransactions();
        availableTransactionModels = TransactionPresenter.getTransactions();
        date.setYear(date.getYear() - 1900);
        textDate.setText(format.format(date));
        transactionModel = TransactionPresenter.getTransactionsForCurrentDate(date, availableTransactionModels);
        setSorts();
        setFilters();
        textView3.setText(account.getTotalLimit() + "");
        textView4.setText(account.getMonthLimit() + "");

        spinSortAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sorts);
        spinnerSort.setAdapter(spinSortAdapter);

        spinFilterAdapter = new TypeListAdapter(this, R.layout.type_element, filters);
        spinnerFilter.setAdapter(spinFilterAdapter);

        adapter = new TransactionListAdapter(this, R.layout.list_element, transactionModel);
        listView.setAdapter(adapter);

        leftBtn.setOnClickListener(listenerLeft);
        rightBtn.setOnClickListener(listenerRight);

        spinnerSort.setOnItemSelectedListener(listenerSort);
        spinnerFilter.setOnItemSelectedListener(listenerFilter);
        TransactionPresenter.sort(transactionModel, sorts.get(0));

        listView.setOnItemClickListener(itemClickListener);

    }
}
