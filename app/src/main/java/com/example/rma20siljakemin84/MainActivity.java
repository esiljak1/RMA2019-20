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
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ITransactionView, IAccountView{
    private ListView listView;
    private Spinner spinnerSort, spinnerFilter;
    private TextView textView3, textView4, textDate;
    private Button button;
    private ImageButton leftBtn, rightBtn;

    private TransactionPresenter presenter = new TransactionPresenter(this);

    private AccountPresenter accountPresenter = presenter.getAccount();
    private TransactionListAdapter adapter;
    private List<String> sorts = new ArrayList<>();
    private List<Type> filters = new ArrayList<>();
    private ArrayAdapter spinSortAdapter;
    private TypeListAdapter spinFilterAdapter;
    private Calendar date = Calendar.getInstance();
    private SimpleDateFormat format = new SimpleDateFormat("MMMM, yyyy");

    private ImageButton.OnClickListener listenerLeft =
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {
                    date.add(Calendar.MONTH, -1);
                    textDate.setText(format.format(date.getTime()));
                    presenter.transactionsForCurrentDate(date);
                    presenter.filter(date, (Type)spinnerFilter.getSelectedItem());
                    presenter.sort((String) spinnerSort.getSelectedItem());
                    adapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, presenter.getCurrentDateTransactions());
                    listView.setAdapter(adapter);
                }
            };
    private ImageButton.OnClickListener listenerRight =
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {
                    date.add(Calendar.MONTH, 1);
                    textDate.setText(format.format(date.getTime()));
                    presenter.transactionsForCurrentDate(date);
                    presenter.filter(date, (Type)spinnerFilter.getSelectedItem());
                    presenter.sort((String) spinnerSort.getSelectedItem());
                    adapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, presenter.getCurrentDateTransactions());
                    listView.setAdapter(adapter);
                }
            };
    private Spinner.OnItemSelectedListener listenerSort =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    presenter.sort(sorts.get(position));
                    adapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, presenter.getCurrentDateTransactions());
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
                    presenter.filter(date, filters.get(position));
                    adapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, presenter.getCurrentDateTransactions());
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
                    editTransactionIntent.putExtra("date", new SimpleDateFormat("dd.MM.yyyy").format(t.getDate().getTime()));
                    editTransactionIntent.putExtra("amount", t.getAmount() + "");
                    editTransactionIntent.putExtra("title", t.getTitle());
                    editTransactionIntent.putExtra("type", t.getType());
                    editTransactionIntent.putExtra("description", t.getItemDescription());
                    editTransactionIntent.putExtra("interval", t.getTransactionInterval() + "");
                    editTransactionIntent.putExtra("global", accountPresenter.getBudget() + "");
                    editTransactionIntent.putExtra("month", accountPresenter.getMonthlyLimit() + "");
                    editTransactionIntent.putExtra("id", t.getId() + "");
                    if(t.getEndDate() != null) {
                        editTransactionIntent.putExtra("endDate", new SimpleDateFormat("dd.MM.yyyy").format(t.getEndDate().getTime()));
                    }
                    MainActivity.this.startActivity(editTransactionIntent);
                }
            };
    private Button.OnClickListener addTransactionListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent addTransactionIntent = new Intent(MainActivity.this, EditTransactionActivity.class);
                    addTransactionIntent.putExtra("dodavanje", "da");
                    addTransactionIntent.putExtra("global", accountPresenter.getBudget() + "");
                    addTransactionIntent.putExtra("month", accountPresenter.getMonthlyLimit() + "");
                    MainActivity.this.startActivity(addTransactionIntent);
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

        listView = (ListView)findViewById(R.id.listView);
        spinnerSort = (Spinner)findViewById(R.id.spinnerSort);
        spinnerFilter = (Spinner)findViewById(R.id.spinnerFilter);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textDate = (TextView)findViewById(R.id.textDate);
        button = (Button)findViewById(R.id.button);
        leftBtn = (ImageButton)findViewById(R.id.leftBtn);
        rightBtn = (ImageButton)findViewById(R.id.rightBtn);
        textDate.setText(format.format(date.getTime()));
        setSorts();
        setFilters();
        textView3.setText(accountPresenter.getBudget() + "");
        textView4.setText(accountPresenter.getMonthlyLimit() + "");

        spinSortAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sorts);
        spinnerSort.setAdapter(spinSortAdapter);

        spinFilterAdapter = new TypeListAdapter(this, R.layout.type_element, filters);
        spinnerFilter.setAdapter(spinFilterAdapter);

        presenter.transactionsForCurrentDate(date);
        adapter = new TransactionListAdapter(this, R.layout.list_element, presenter.getCurrentDateTransactions());
        listView.setAdapter(adapter);

        leftBtn.setOnClickListener(listenerLeft);
        rightBtn.setOnClickListener(listenerRight);

        spinnerSort.setOnItemSelectedListener(listenerSort);
        spinnerFilter.setOnItemSelectedListener(listenerFilter);

        listView.setOnItemClickListener(itemClickListener);
        button.setOnClickListener(addTransactionListener);

    }

    @Override
    protected void onResume() {
        spinnerFilter.setSelection(0);
        spinnerSort.setSelection(0);
        presenter.transactionsForCurrentDate(date);
        adapter = new TransactionListAdapter(this, R.layout.list_element, presenter.getCurrentDateTransactions());
        listView.setAdapter(adapter);
        textView3.setText(accountPresenter.getBudget() + "");
        super.onResume();
    }
}
