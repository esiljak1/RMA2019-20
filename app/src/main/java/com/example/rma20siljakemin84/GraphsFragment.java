package com.example.rma20siljakemin84;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;


public class GraphsFragment extends Fragment implements ITransactionView{
    private static final int MIN_DISTANCE = 900;
    private static final int mjeseci = 12;
    private static final int dani = 365;
    private static final int sedmice = 52;

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
    private Spinner.OnItemSelectedListener spinnerListener =
            new Spinner.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                        {
                            setIncomeChartForDays();
                            setSpendingChartForDays();
                            setTotalChartForDays();
                            break;
                        } case 1:
                        {
                            setIncomeChartForWeeks();
                            setSpendingChartForWeeks();
                            setTotalChartForWeeks();
                            break;
                        }
                        case 2:
                        {
                            setIncomeChartForMonths();
                            setSpendingChartForMonths();
                            setTotalChartForMonths();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
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
        incomeChart.notifyDataSetChanged();
        incomeChart.invalidate();
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
        spendingChart.notifyDataSetChanged();
        spendingChart.invalidate();
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
        totalChart.notifyDataSetChanged();
        totalChart.invalidate();
    }

    private void setIncomeChartForWeeks(){
        int vel = sedmice;
        barEntryArrayList = new ArrayList<>();
        if(checkNumberOfWeeks()){
            vel++;
        }
        for(int i = 0; i < vel; i++){
            double vrijednost = presenter.getIncomeForWeek(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float)vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Income");
        barDataSet.setColor(Color.GREEN);
        BarData barData = new BarData(barDataSet);
        incomeChart.setData(barData);
        incomeChart.notifyDataSetChanged();
        incomeChart.invalidate();
    }

    private void setSpendingChartForWeeks(){
        int vel = sedmice;
        barEntryArrayList = new ArrayList<>();
        if(checkNumberOfWeeks()){
            vel++;
        }
        for(int i = 0; i < vel; i++){
            double vrijednost = presenter.getSpendingForWeek(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float)vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Spending");
        barDataSet.setColor(Color.RED);
        BarData barData = new BarData(barDataSet);
        spendingChart.setData(barData);
        spendingChart.notifyDataSetChanged();
        spendingChart.invalidate();
    }

    private void setTotalChartForWeeks(){
        int vel = sedmice;
        double vrijednost = 0;
        barEntryArrayList = new ArrayList<>();
        if(checkNumberOfWeeks()){
            vel++;
        }
        for(int i = 0; i < vel; i++){
            vrijednost += presenter.getIncomeForWeek(i);
            vrijednost -= presenter.getSpendingForWeek(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Total");
        barDataSet.setColor(Color.BLUE);
        BarData barData = new BarData(barDataSet);
        totalChart.setData(barData);
        totalChart.notifyDataSetChanged();
        totalChart.invalidate();
    }

    private void setIncomeChartForDays(){
        int vel = dani;
        barEntryArrayList = new ArrayList<>();
        if(Calendar.getInstance().get(Calendar.YEAR) % 4 == 0){
            vel++;
        }
        for(int i = 0; i < vel; i++){
            double vrijednost = presenter.getIncomeForDay(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Income");
        barDataSet.setColor(Color.GREEN);
        BarData barData = new BarData(barDataSet);
        incomeChart.setData(barData);
        incomeChart.notifyDataSetChanged();
        incomeChart.invalidate();
    }

    private void setSpendingChartForDays(){
        int vel = dani;
        barEntryArrayList = new ArrayList<>();
        if(Calendar.getInstance().get(Calendar.YEAR) % 4 == 0){
            vel++;
        }
        for(int i = 0; i < vel; i++){
            double vrijednost = presenter.getSpendingForDay(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Spending");
        barDataSet.setColor(Color.RED);
        BarData barData = new BarData(barDataSet);
        spendingChart.setData(barData);
        spendingChart.notifyDataSetChanged();
        spendingChart.invalidate();
    }

    private void setTotalChartForDays(){
        int vel = dani;
        double vrijednost = 0;
        barEntryArrayList = new ArrayList<>();
        if(Calendar.getInstance().get(Calendar.YEAR) % 4 == 0){
            vel++;
        }
        for(int i = 0; i < vel; i++){
            vrijednost += presenter.getIncomeForDay(i);
            vrijednost -= presenter.getSpendingForDay(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Total");
        barDataSet.setColor(Color.BLUE);
        BarData barData = new BarData(barDataSet);
        totalChart.setData(barData);
        totalChart.notifyDataSetChanged();
        totalChart.invalidate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_graphs, container, false);

        spinnerGraphicChooser = view.findViewById(R.id.spinnerGraphChooser);
        spendingChart = view.findViewById(R.id.spendingChart);
        incomeChart = view.findViewById(R.id.incomeChart);
        totalChart = view.findViewById(R.id.totalChart);

        presenter = ((MainActivity) getActivity()).getPresenter();

        setSpinnerData();
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerItems);
        spinnerGraphicChooser.setAdapter(spinnerAdapter);
        spinnerGraphicChooser.setSelection(2);

        view.setOnTouchListener(swipeGesture);
        spinnerGraphicChooser.setOnItemSelectedListener(spinnerListener);

        return view;
    }
}
