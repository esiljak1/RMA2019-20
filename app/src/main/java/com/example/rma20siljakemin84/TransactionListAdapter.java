package com.example.rma20siljakemin84;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TransactionListAdapter extends ArrayAdapter<Transaction> {
    private int resource;
    public TransactionListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Transaction[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout newView;
        if(convertView == null){
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        }else{
            newView = (LinearLayout)convertView;
        }
        Transaction instance = getItem(position);
        //postavljanje odgovarajucih vrijednosti u listview
        return newView;
    }
}

