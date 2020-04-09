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


public class GraphsFragment extends Fragment {
    private static final int mjeseci = 12;
    private static int dani = 31;
    private static int sedmice = 5; //nisam siguran koliko sedmica

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
                            double current = event.getX();
                            if(oldTouchValue < current){
                                if(getActivity().findViewById(R.id.transaction_details) != null){
                                    return false;
                                }
                                AccountDetailsFragment detailsFragment = new AccountDetailsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("transaction", presenter);
                                detailsFragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, detailsFragment).commit();
                            }
                            if(oldTouchValue > current){
                                if(getActivity().findViewById(R.id.transaction_details) != null){
                                    return false;
                                }
                                TransactionListFragment listFragment = new TransactionListFragment();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list, listFragment).commit();
                            }
                            return true;
                        }
                    }
                    return false;
                }
            };

    private void setSpinnerData(){
        spinnerItems.add("by day");
        spinnerItems.add("by week");
        spinnerItems.add("by month");
    }

    private void setIncomeChartForMonths(){
        for(int i = 0; i < mjeseci; i++){
            double vrijednost = presenter.getIncomeForMonth(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Income");
        barDataSet.setColor(Color.GREEN);
        BarData barData = new BarData(barDataSet);
        incomeChart.setData(barData);
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

        setIncomeChartForMonths();

        view.setOnTouchListener(swipeGesture);

        //TODO dodati minimalnu vrijednost koju prst mora preci da se detektuje swipe

        return view;
    }
}
