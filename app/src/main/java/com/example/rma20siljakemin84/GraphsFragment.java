package com.example.rma20siljakemin84;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;


public class GraphsFragment extends Fragment {
    private static final int MIN_DISTANCE = 900;
    private static final int mjeseci = 12;
    private static final int dani = 365;
    private static final int sedmice = 52; //nisam siguran koliko sedmica

    private Spinner spinnerGraphicChooser;
    private BarChart spendingChart, incomeChart, totalChart;

    private ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();

    private TransactionPresenter presenter;
    private ArrayList<String> spinnerItems = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private double oldTouchValue = 0;

    private View view;
    private View.OnTouchListener swipeGesture =
            new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
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
                                    if (getActivity().findViewById(R.id.transaction_details) != null) {
                                        return false;
                                    }
                                    AccountDetailsFragment detailsFragment = new AccountDetailsFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("transaction", presenter);
                                    detailsFragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, detailsFragment).commit();
                                }
                                if (deltaX > 0) {
                                    if (getActivity().findViewById(R.id.transaction_details) != null) {
                                        return false;
                                    }
                                    TransactionListFragment listFragment = new TransactionListFragment();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, listFragment).commit();
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };

    private boolean checkNumberOfWeeks(){
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.YEAR) % 4 == 0){
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY;
        }else{
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY;
        }
    }

    private void setSpinnerData(){
        spinnerItems.add("by day");
        spinnerItems.add("by week");
        spinnerItems.add("by month");
    }

    private void setIncomeChartForMonths(){
        barEntryArrayList = new ArrayList<>();
        for(int i = 0; i < mjeseci; i++){
            double vrijednost = presenter.getIncomeForMonth(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Income");
        barDataSet.setColor(Color.GREEN);
        BarData barData = new BarData(barDataSet);
        incomeChart.setData(barData);
    }

    private void setSpendingChartForMonths(){
        barEntryArrayList = new ArrayList<>();
        for(int i = 0; i < mjeseci; i++){
            double vrijednost = presenter.getSpendingForMonth(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Spending");
        barDataSet.setColor(Color.RED);
        BarData barData = new BarData(barDataSet);
        spendingChart.setData(barData);
    }

    private void setTotalChartForMonths(){
        barEntryArrayList = new ArrayList<>();
        double vrijednost = 0;
        for(int i = 0; i < mjeseci; i++){
            vrijednost += presenter.getIncomeForMonth(i);
            vrijednost -= presenter.getSpendingForMonth(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Total");
        barDataSet.setColor(Color.BLUE);
        BarData barData = new BarData(barDataSet);
        totalChart.setData(barData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_graphs, container, false);

        spinnerGraphicChooser = view.findViewById(R.id.spinnerGraphChooser);
        spendingChart = view.findViewById(R.id.spendingChart);
        incomeChart = view.findViewById(R.id.incomeChart);
        totalChart = view.findViewById(R.id.totalChart);

        presenter = getArguments().getParcelable("transaction");

        setSpinnerData();
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerItems);
        spinnerGraphicChooser.setAdapter(spinnerAdapter);
        spinnerGraphicChooser.setSelection(2);

        checkNumberOfWeeks();

        setIncomeChartForMonths();
        setSpendingChartForMonths();
        setTotalChartForMonths();

        view.setOnTouchListener(swipeGesture);

        return view;
    }
}
