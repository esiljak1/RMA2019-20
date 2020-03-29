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


public class MainActivity extends AppCompatActivity implements ITransactionView{
    private ListView listViewTransactions;
    private Spinner spinnerSort, spinnerFilter;
    private TextView textBudget, textLimit, textDate;
    private Button addTransactionBtn;
    private ImageButton leftBtn, rightBtn;

    private TransactionPresenter presenter = new TransactionPresenter(this);

    private TransactionListAdapter transactionsAdapter;
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
                    date.add(Calendar.MONTH, -1);       //pritiskom ili na lijevoili na desno dugme vrsi se ponovno filtiranje/sortiranje u zavisnosti koji je item
                    textDate.setText(format.format(date.getTime())); //selectovan u spinnerima
                    presenter.transactionsForCurrentDate(date);
                    presenter.filter(date, (Type)spinnerFilter.getSelectedItem());
                    presenter.sort((String) spinnerSort.getSelectedItem());
                    transactionsAdapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, presenter.getCurrentDateTransactions());
                    listViewTransactions.setAdapter(transactionsAdapter);
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
                    transactionsAdapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, presenter.getCurrentDateTransactions());
                    listViewTransactions.setAdapter(transactionsAdapter);
                }
            };
    private Spinner.OnItemSelectedListener listenerSort =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    presenter.sort(sorts.get(position));
                    transactionsAdapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, presenter.getCurrentDateTransactions());
                    listViewTransactions.setAdapter(transactionsAdapter);
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
                    transactionsAdapter = new TransactionListAdapter(MainActivity.this, R.layout.list_element, presenter.getCurrentDateTransactions());
                    listViewTransactions.setAdapter(transactionsAdapter);
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
                    TransactionModel t = transactionsAdapter.getItem(position);
                    editTransactionIntent.putExtra("date", new SimpleDateFormat("dd.MM.yyyy").format(t.getDate().getTime()));
                    editTransactionIntent.putExtra("amount", t.getAmount() + "");
                    editTransactionIntent.putExtra("title", t.getTitle());
                    editTransactionIntent.putExtra("type", t.getType());
                    editTransactionIntent.putExtra("description", t.getItemDescription());
                    editTransactionIntent.putExtra("interval", t.getTransactionInterval() + "");
                    editTransactionIntent.putExtra("global", presenter.getAccount().getBudget() + "");  //budzet
                    editTransactionIntent.putExtra("month", presenter.getAccount().getMonthlyLimit()+ "");  //monthLimit
                    editTransactionIntent.putExtra("limit", presenter.getAccount().getOverallLimit() + ""); //overallLimit
                    editTransactionIntent.putExtra("id", t.getId() + "");   //jedinstveni id po kojem se razlikuju transakcije radi lakseg update-a
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
                    addTransactionIntent.putExtra("global", presenter.getAccount().getBudget() + "");
                    addTransactionIntent.putExtra("limit", presenter.getAccount().getOverallLimit() + "");
                    addTransactionIntent.putExtra("month", presenter.getAccount().getMonthlyLimit() + "");
                    MainActivity.this.startActivity(addTransactionIntent);
                }
            };

    private void setSortList(){
        sorts.add("Sort by");
        sorts.add("Price - Ascending");
        sorts.add("Price - Descending");
        sorts.add("Title - Ascending");
        sorts.add("Title - Descending");
        sorts.add("Date - Ascending");
        sorts.add("Date - Descending");
    }
    private void setFilterList(){
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

        listViewTransactions = (ListView)findViewById(R.id.listViewTransactions);
        spinnerSort = (Spinner)findViewById(R.id.spinnerSort);
        spinnerFilter = (Spinner)findViewById(R.id.spinnerFilter);
        textBudget = (TextView)findViewById(R.id.textBudget);
        textLimit = (TextView)findViewById(R.id.textLimit);
        textDate = (TextView)findViewById(R.id.textDate);
        addTransactionBtn = (Button)findViewById(R.id.addTransactionBtn);
        leftBtn = (ImageButton)findViewById(R.id.leftBtn);
        rightBtn = (ImageButton)findViewById(R.id.rightBtn);
        textDate.setText(format.format(date.getTime()));
        setSortList();
        setFilterList();
        textBudget.setText(presenter.getAccount().getBudget() + "");
        textLimit.setText(presenter.getAccount().getOverallLimit() + "");

        spinSortAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sorts);
        spinnerSort.setAdapter(spinSortAdapter);

        spinFilterAdapter = new TypeListAdapter(this, R.layout.type_element, filters);
        spinnerFilter.setAdapter(spinFilterAdapter);

        presenter.transactionsForCurrentDate(date);
        transactionsAdapter = new TransactionListAdapter(this, R.layout.list_element, presenter.getCurrentDateTransactions());
        listViewTransactions.setAdapter(transactionsAdapter);

        leftBtn.setOnClickListener(listenerLeft);
        rightBtn.setOnClickListener(listenerRight);

        spinnerSort.setOnItemSelectedListener(listenerSort);
        spinnerFilter.setOnItemSelectedListener(listenerFilter);

        listViewTransactions.setOnItemClickListener(itemClickListener);
        addTransactionBtn.setOnClickListener(addTransactionListener);

    }

    @Override
    protected void onResume() {
        spinnerFilter.setSelection(0);
        spinnerSort.setSelection(0);
        presenter.transactionsForCurrentDate(date);
        transactionsAdapter = new TransactionListAdapter(this, R.layout.list_element, presenter.getCurrentDateTransactions());
        listViewTransactions.setAdapter(transactionsAdapter);
        textBudget.setText(presenter.getAccount().getBudget() + "");
        super.onResume();
    }
}
