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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;


public class GraphsFragment extends Fragment implements ITransactionView{
    private static final int MIN_DISTANCE = 900;
    private static final int mjeseci = 12;
    private static int dani = 30;
    private static int sedmice = 4;

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
                    ((MainActivity) getActivity()).getPresenter().setView(GraphsFragment.this);
                    ((MainActivity) getActivity()).getPresenter().getTransactions("", "", "", "");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    private void setDays(int month){        //postavlja broj dana u zavisnosti od mjeseca
        if(month < 7 && month % 2 == 0) dani = 31;
        else if(month >= 7 && month % 2 == 1) dani = 31;
        else if(month == 1) {
            dani = 28;
            if(Calendar.getInstance().get(Calendar.YEAR) % 4 == 0) dani++;
        }
        else dani = 30;
    }

    private void setWeeks(int month){       //postavlja broj sedmica u zavisnosti od mjeseca
        Calendar prvi = Calendar.getInstance();
        prvi.set(Calendar.MONTH, month);
        prvi.set(Calendar.DAY_OF_MONTH, 1);

        sedmice = prvi.getActualMaximum(Calendar.WEEK_OF_MONTH);
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
        Description description = new Description();
        description.setText("Income");
        incomeChart.setDescription(description);
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
        Description description = new Description();
        description.setText("Spending");
        spendingChart.setDescription(description);
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
        Description description = new Description();
        description.setText("Total");
        totalChart.setDescription(description);
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Total");
        barDataSet.setColor(Color.BLUE);
        BarData barData = new BarData(barDataSet);
        totalChart.setData(barData);
        totalChart.notifyDataSetChanged();
        totalChart.invalidate();
    }

    private void setIncomeChartForWeeks(){
        setWeeks(Calendar.getInstance().get(Calendar.MONTH));
        barEntryArrayList = new ArrayList<>();
        for(int i = 0; i < sedmice; i++){
            double vrijednost = presenter.getIncomeForWeek(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float)vrijednost));
        }
        Description description = new Description();
        description.setText("Income");
        incomeChart.setDescription(description);
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Income");
        barDataSet.setColor(Color.GREEN);
        BarData barData = new BarData(barDataSet);
        incomeChart.setData(barData);
        incomeChart.notifyDataSetChanged();
        incomeChart.invalidate();
    }

    private void setSpendingChartForWeeks(){
        setWeeks(Calendar.getInstance().get(Calendar.MONTH));
        barEntryArrayList = new ArrayList<>();
        for(int i = 0; i < sedmice; i++){
            double vrijednost = presenter.getSpendingForWeek(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float)vrijednost));
        }
        Description description = new Description();
        description.setText("Spending");
        spendingChart.setDescription(description);
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Spending");
        barDataSet.setColor(Color.RED);
        BarData barData = new BarData(barDataSet);
        spendingChart.setData(barData);
        spendingChart.notifyDataSetChanged();
        spendingChart.invalidate();
    }

    private void setTotalChartForWeeks(){
        setWeeks(Calendar.getInstance().get(Calendar.MONTH));
        barEntryArrayList = new ArrayList<>();
        double vrijednost = 0;
        for(int i = 0; i < sedmice; i++){
            vrijednost += presenter.getIncomeForWeek(i);
            vrijednost -= presenter.getSpendingForWeek(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        Description description = new Description();
        description.setText("Total");
        totalChart.setDescription(description);
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Total");
        barDataSet.setColor(Color.BLUE);
        BarData barData = new BarData(barDataSet);
        totalChart.setData(barData);
        totalChart.notifyDataSetChanged();
        totalChart.invalidate();
    }

    private void setIncomeChartForDays(){
        setDays(Calendar.getInstance().get(Calendar.MONTH));
        barEntryArrayList = new ArrayList<>();
        for(int i = 0; i < dani; i++){
            double vrijednost = presenter.getIncomeForDay(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        Description description = new Description();
        description.setText("Income");
        incomeChart.setDescription(description);
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Income");
        barDataSet.setColor(Color.GREEN);
        BarData barData = new BarData(barDataSet);
        incomeChart.setData(barData);
        incomeChart.notifyDataSetChanged();
        incomeChart.invalidate();
    }

    private void setSpendingChartForDays(){
        setDays(Calendar.getInstance().get(Calendar.MONTH));
        barEntryArrayList = new ArrayList<>();
        for(int i = 0; i < dani; i++){
            double vrijednost = presenter.getSpendingForDay(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        Description description = new Description();
        description.setText("Spending");
        spendingChart.setDescription(description);
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Spending");
        barDataSet.setColor(Color.RED);
        BarData barData = new BarData(barDataSet);
        spendingChart.setData(barData);
        spendingChart.notifyDataSetChanged();
        spendingChart.invalidate();
    }

    private void setTotalChartForDays(){
        setDays(Calendar.getInstance().get(Calendar.MONTH));
        barEntryArrayList = new ArrayList<>();
        double vrijednost = 0;
        for(int i = 0; i < dani; i++){
            vrijednost += presenter.getIncomeForDay(i);
            vrijednost -= presenter.getSpendingForDay(i);
            barEntryArrayList.add(new BarEntry(i + 1, (float) vrijednost));
        }
        Description description = new Description();
        description.setText("Total");
        totalChart.setDescription(description);
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

    @Override
    public void notifyTransactionsChanged() {
        int broj = ((MainActivity) getActivity()).getPresenter().getCurrentDateTransactions().size();
        if(spinnerGraphicChooser.getSelectedItem().equals("by day")){
            System.out.println("Velicina: " + broj);
            setIncomeChartForDays();
            setSpendingChartForDays();
            setTotalChartForDays();
        }else if(spinnerGraphicChooser.getSelectedItem().equals("by week")){
            System.out.println("Velicina: " + broj);
            setIncomeChartForWeeks();
            setSpendingChartForWeeks();
            setTotalChartForWeeks();
        }else{
            System.out.println("Velicina: " + broj);
            setIncomeChartForMonths();
            setSpendingChartForMonths();
            setTotalChartForMonths();
        }
    }

    @Override
    public void notifyAddedTransaction(TransactionModel transaction) {

    }
}
