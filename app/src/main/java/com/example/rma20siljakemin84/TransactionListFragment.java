package com.example.rma20siljakemin84;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionListFragment extends Fragment {
    private ListView listViewTransactions;
    private Spinner spinnerSort, spinnerFilter;
    private TextView textBudget, textLimit, textDate;
    private Button addTransactionBtn;
    private ImageButton leftBtn, rightBtn;
    private int selectedItem = -1;

    private TransactionPresenter presenter = new TransactionPresenter(new MainActivity());

    private TransactionListAdapter transactionsAdapter;
    private List<String> sorts = new ArrayList<>();
    private List<Type> filters = new ArrayList<>();
    private ArrayAdapter spinSortAdapter;
    private TypeListAdapter spinFilterAdapter;
    private Calendar date = Calendar.getInstance();
    private SimpleDateFormat format = new SimpleDateFormat("MMMM, yyyy");

    private View view;
    private double oldTouchValue = 0;

    private ImageButton.OnClickListener listenerLeft =
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {
                    date.add(Calendar.MONTH, -1);       //pritiskom ili na lijevoili na desno dugme vrsi se ponovno filtiranje/sortiranje u zavisnosti koji je item
                    textDate.setText(format.format(date.getTime())); //selectovan u spinnerima
                    presenter.transactionsForCurrentDate(date);
                    presenter.filter(date, (Type)spinnerFilter.getSelectedItem());
                    presenter.sort((String) spinnerSort.getSelectedItem());
                    updateList();
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
                    updateList();
                }
            };
    private Spinner.OnItemSelectedListener listenerSort =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    presenter.sort(sorts.get(position));
                    updateList();
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
                    updateList();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
    private ListView.OnItemClickListener itemClickListener =
            new ListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(selectedItem == position){
                        listViewTransactions.setAdapter(transactionsAdapter);
                        selectedItem = -1;
                        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transaction_details, detailFragment).addToBackStack(null).commit();
                        return;
                    }
                    selectedItem = position;
                    TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                    TransactionModel t = transactionsAdapter.getItem(position);
                    Bundle args = new Bundle();
                    args.putString("date", new SimpleDateFormat("dd.MM.yyyy").format(t.getDate().getTime()));
                    args.putString("amount", t.getAmount() + "");
                    args.putString("title", t.getTitle());
                    args.putSerializable("type", t.getType());
                    args.putString("description", t.getItemDescription());
                    args.putString("interval", t.getTransactionInterval() + "");
                    args.putString("global", presenter.getAccount().getBudget() + "");  //budzet
                    args.putString("month", presenter.getAccount().getMonthlyLimit()+ "");  //monthLimit
                    args.putString("limit", presenter.getAccount().getOverallLimit() + ""); //overallLimit
                    args.putString("id", t.getId() + "");   //jedinstveni id po kojem se razlikuju transakcije radi lakseg update-a
                    if(t.getEndDate() != null) {
                        args.putString("endDate", new SimpleDateFormat("dd.MM.yyyy").format(t.getEndDate().getTime()));
                    }
                    detailFragment.setArguments(args);
                    if(getActivity().findViewById(R.id.transaction_details) != null){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transaction_details, detailFragment).addToBackStack(null).commit();
                    }else {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, detailFragment).addToBackStack(null).commit();
                    }
                }
            };
    private Button.OnClickListener addTransactionListener =
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("dodavanje", "da");
                    bundle.putString("global", presenter.getAccount().getBudget() + "");
                    bundle.putString("limit", presenter.getAccount().getOverallLimit() + "");
                    bundle.putString("month", presenter.getAccount().getMonthlyLimit() + "");
                    detailFragment.setArguments(bundle);
                    if(getActivity().findViewById(R.id.transaction_details) != null){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transaction_details, detailFragment).addToBackStack(null).commit();
                    }else{
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, detailFragment).addToBackStack(null).commit();
                    }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        listViewTransactions = (ListView)view.findViewById(R.id.listViewTransactions);
        spinnerSort = (Spinner)view.findViewById(R.id.spinnerSort);
        spinnerFilter = (Spinner)view.findViewById(R.id.spinnerFilter);
        textBudget = (TextView)view.findViewById(R.id.textBudget);
        textLimit = (TextView)view.findViewById(R.id.textLimit);
        textDate = (TextView)view.findViewById(R.id.textDate);
        addTransactionBtn = (Button)view.findViewById(R.id.addTransactionBtn);
        leftBtn = (ImageButton)view.findViewById(R.id.leftBtn);
        rightBtn = (ImageButton)view.findViewById(R.id.rightBtn);
        textDate.setText(format.format(date.getTime()));
        setSortList();
        setFilterList();
        textBudget.setText(presenter.getAccount().getBudget() + "");
        textLimit.setText(presenter.getAccount().getOverallLimit() + "");

        spinSortAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, sorts);
        spinnerSort.setAdapter(spinSortAdapter);

        spinFilterAdapter = new TypeListAdapter(getContext(), R.layout.type_element, filters);
        spinnerFilter.setAdapter(spinFilterAdapter);

        presenter.transactionsForCurrentDate(date);
        transactionsAdapter = new TransactionListAdapter(getContext(), R.layout.list_element, presenter.getCurrentDateTransactions());
        listViewTransactions.setAdapter(transactionsAdapter);

        leftBtn.setOnClickListener(listenerLeft);
        rightBtn.setOnClickListener(listenerRight);

        spinnerSort.setOnItemSelectedListener(listenerSort);
        spinnerFilter.setOnItemSelectedListener(listenerFilter);

        listViewTransactions.setOnItemClickListener(itemClickListener);
        addTransactionBtn.setOnClickListener(addTransactionListener);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                    {
                        oldTouchValue = event.getX();
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        double current = event.getX();
                        if(oldTouchValue < current){
                            System.out.println("Lijevi swipe");
                        }
                        if(oldTouchValue > current){
                            if(getActivity().findViewById(R.id.transaction_details) != null){
                                return false;
                            }
                            AccountDetailsFragment detailsFragment = new AccountDetailsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("account", presenter.getAccount());
                            detailsFragment.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, detailsFragment).addToBackStack(null).commit();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }
    public void updateList(){
        transactionsAdapter = new TransactionListAdapter(getContext(), R.layout.list_element, presenter.getCurrentDateTransactions());
        listViewTransactions.setAdapter(transactionsAdapter);
    }

    public TransactionPresenter getPresenter() {
        return presenter;
    }
}
