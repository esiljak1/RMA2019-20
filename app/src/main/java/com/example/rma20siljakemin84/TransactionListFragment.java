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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionListFragment extends Fragment implements ITransactionView, IAccountView{
    private static final int MIN_DISTANCE = 800;

    private ListView listViewTransactions;
    private Spinner spinnerSort, spinnerFilter;
    private TextView textBudget, textLimit, textDate;
    private Button addTransactionBtn;
    private ImageButton leftBtn, rightBtn;
    private ConstraintLayout transactionList;
    private int selectedItem = -1;

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
                    date.add(Calendar.MONTH, -1);       //pritiskom ili na lijevo ili na desno dugme vrsi se ponovno filtiranje/sortiranje u zavisnosti koji je item
                    textDate.setText(format.format(date.getTime())); //selectovan u spinnerima
                    ((MainActivity) getActivity()).getPresenter().getTransactions(getTransactionTypeStringKey(((Type) spinnerFilter.getSelectedItem())),
                            getSortKey(((String) spinnerSort.getSelectedItem())), "", "");
                    //notifyTransactionsChanged();
                }
            };
    private ImageButton.OnClickListener listenerRight =
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View v) {
                    date.add(Calendar.MONTH, 1);
                    textDate.setText(format.format(date.getTime()));
                    ((MainActivity) getActivity()).getPresenter().getTransactions(getTransactionTypeStringKey(((Type) spinnerFilter.getSelectedItem())),
                            getSortKey(((String) spinnerSort.getSelectedItem())), "", "");
                    //notifyTransactionsChanged();
                }
            };
    private Spinner.OnItemSelectedListener listenerSort =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((MainActivity) getActivity()).getPresenter().getTransactions(getTransactionTypeStringKey(((Type) spinnerFilter.getSelectedItem())),
                            getSortKey(sorts.get(position)), "", "");
                    //notifyTransactionsChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
    private Spinner.OnItemSelectedListener listenerFilter =
            new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((MainActivity) getActivity()).getPresenter().getTransactions(getTransactionTypeStringKey(filters.get(position)),
                            getSortKey(((String) spinnerSort.getSelectedItem())), "", "");
                    spinnerSort.setSelection(0);
                    //notifyTransactionsChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
    private ListView.OnItemClickListener itemClickListener =
            new ListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(((MainActivity) getActivity()).isTwoPaneMode() && selectedItem == position){
                        listViewTransactions.setAdapter(transactionsAdapter);
                        selectedItem = -1;
                        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("global", ((MainActivity) getActivity()).getPresenter().getAccount().getBudget() + "");
                        bundle.putString("limit", ((MainActivity) getActivity()).getPresenter().getAccount().getOverallLimit() + "");
                        bundle.putString("month", ((MainActivity) getActivity()).getPresenter().getAccount().getOverallLimit() + "");
                        bundle.putString("dodavanje", "da");
                        detailFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transaction_details, detailFragment).commit();
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
                    args.putString("global", ((MainActivity) getActivity()).getPresenter().getAccount().getBudget() + "");  //budzet
                    args.putString("month", ((MainActivity) getActivity()).getPresenter().getAccount().getMonthlyLimit()+ "");  //monthLimit
                    args.putString("limit", ((MainActivity) getActivity()).getPresenter().getAccount().getOverallLimit() + ""); //overallLimit
                    args.putString("id", t.getId() + "");   //jedinstveni id po kojem se razlikuju transakcije radi lakseg update-a
                    if(t.getEndDate() != null) {
                        args.putString("endDate", new SimpleDateFormat("dd.MM.yyyy").format(t.getEndDate().getTime()));
                    }
                    detailFragment.setArguments(args);
                    if(((MainActivity) getActivity()).isTwoPaneMode()){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transaction_details, detailFragment).commit();
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
                    bundle.putString("global", ((MainActivity) getActivity()).getPresenter().getAccount().getBudget() + "");
                    bundle.putString("limit", ((MainActivity) getActivity()).getPresenter().getAccount().getOverallLimit() + "");
                    bundle.putString("month", ((MainActivity) getActivity()).getPresenter().getAccount().getMonthlyLimit() + "");
                    detailFragment.setArguments(bundle);
                    if(((MainActivity) getActivity()).isTwoPaneMode()){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transaction_details, detailFragment).addToBackStack(null).commit();
                    }else{
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, detailFragment).addToBackStack(null).commit();
                    }
                }
            };
    private View.OnTouchListener swipeListener =
            new View.OnTouchListener(){

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
                            double deltaX = oldTouchValue - event.getX();
                            if(Math.abs(deltaX) > MIN_DISTANCE) {
                                if (deltaX < 0) {
                                    if (((MainActivity) getActivity()).isTwoPaneMode()) {
                                        return false;
                                    }
                                    GraphsFragment graphsFragment = new GraphsFragment();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, graphsFragment).commit();
                                }
                                if (deltaX > 0) {
                                    if (((MainActivity) getActivity()).isTwoPaneMode()) {
                                        return false;
                                    }
                                    AccountDetailsFragment detailsFragment = new AccountDetailsFragment();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, detailsFragment).commit();
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };

    private void setSortList(){
        sorts.clear();
        sorts.add("Sort by");
        sorts.add("Amount - Ascending");
        sorts.add("Amount - Descending");
        sorts.add("Title - Ascending");
        sorts.add("Title - Descending");
        sorts.add("Date - Ascending");
        sorts.add("Date - Descending");
        spinSortAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, sorts);
    }
    private void setFilterList(){
        filters.clear();
        filters.add(Type.Dummy);
        filters.add(Type.INDIVIDUALPAYMENT);
        filters.add(Type.REGULARPAYMENT);
        filters.add(Type.PURCHASE);
        filters.add(Type.INDIVIDUALINCOME);
        filters.add(Type.REGULARINCOME);
        spinFilterAdapter = new TypeListAdapter(getContext(), R.layout.type_element, filters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        if(((MainActivity) getActivity()).getPresenter() == null){
            ((MainActivity) getActivity()).setPresenter(new TransactionPresenter(this, getString(R.string.root), getString(R.string.api_id)));
        }
        else{
            ((MainActivity) getActivity()).getPresenter().setView(this);
        }

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

        if(!((MainActivity) getActivity()).isTwoPaneMode()){
            ((MainActivity) getActivity()).getPresenter().getAccount().setView(this);
        }
        ((MainActivity)getActivity()).getPresenter().getAccount().getDetailsForAccount(((MainActivity) getActivity()).isConnectedToTheInternet());

        textBudget.setText(String.format("%.2f", ((MainActivity) getActivity()).getPresenter().getAccount().getBudget()));
        textLimit.setText(String.format("%.2f", ((MainActivity) getActivity()).getPresenter().getAccount().getOverallLimit()));

        spinSortAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, sorts);
        spinnerSort.setAdapter(spinSortAdapter);

        spinFilterAdapter = new TypeListAdapter(getContext(), R.layout.type_element, filters);
        spinnerFilter.setAdapter(spinFilterAdapter);

        transactionsAdapter = new TransactionListAdapter(getContext(), R.layout.list_element, ((MainActivity) getActivity()).getPresenter().getCurrentDateTransactions());
        listViewTransactions.setAdapter(transactionsAdapter);

            ((MainActivity)getActivity()).getPresenter().getTransactions(getTransactionTypeStringKey(((Type) spinnerFilter.getSelectedItem())),
                    getSortKey(((String) spinnerSort.getSelectedItem())), "", "");

        leftBtn.setOnClickListener(listenerLeft);
        rightBtn.setOnClickListener(listenerRight);

        spinnerSort.setOnItemSelectedListener(listenerSort);
        spinnerFilter.setOnItemSelectedListener(listenerFilter);

        listViewTransactions.setOnItemClickListener(itemClickListener);
        addTransactionBtn.setOnClickListener(addTransactionListener);

        view.setOnTouchListener(swipeListener);

        return view;
    }

    @Override
    public void notifyTransactionsChanged() {
        if(getActivity() == null || ((MainActivity) getActivity()).getPresenter() == null) return;
        ((MainActivity) getActivity()).getPresenter().transactionsForCurrentDate(date);
        transactionsAdapter = new TransactionListAdapter(getContext(), R.layout.list_element, ((MainActivity) getActivity()).getPresenter().getCurrentDateTransactions());
        listViewTransactions.setAdapter(transactionsAdapter);
    }

    @Override
    public void notifyAddedTransaction(TransactionModel transaction) {
        date.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        ((MainActivity)getActivity()).getPresenter().getTransactions("",
                getSortKey(((String) spinnerSort.getSelectedItem())),"", "");
    }

    @Override
    public void notifyAccountDetailsChanged() {
        if(getActivity() == null || ((MainActivity) getActivity()).getPresenter() == null) return;
        textBudget.setText(String.format("%.2f", ((MainActivity) getActivity()).getPresenter().getAccount().getBudget()));
        textLimit.setText(String.format("%.2f", ((MainActivity) getActivity()).getPresenter().getAccount().getOverallLimit()));
    }

    private String getTransactionTypeStringKey(Type type){
        if(type.equals(Type.Dummy)){
            return "";
        }else{
            return type.getValue() + "";
        }
    }

    private String getSortKey(String sortBy){
        if(sortBy.equals("Sort by")){
            return "";
        }else{
            String[] arr = sortBy.split("-");
            String ret = arr[0].trim().toLowerCase();
            ret += "." + arr[1].trim().toLowerCase().split("c")[0] + "c";
            return ret;
        }
    }

    private String getDoubleDigit(int number){
        if(number < 10){
            return "0" + number;
        }return number + "";
    }

    private String getMonthKey(){
        int month = date.get(Calendar.MONTH) + 1;
        return getDoubleDigit(month);
    }

    private String getYearKey(){
        return date.get(Calendar.YEAR) + "";
    }
}
