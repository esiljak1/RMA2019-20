package com.example.rma20siljakemin84;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TransactionListAdapter extends ArrayAdapter<Transaction> {
    private int resource;

    public TransactionListAdapter(@NonNull Context context, int resource, @NonNull List<Transaction> objects) {
        super(context, resource, objects);
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

        TextView title = (TextView) newView.findViewById(R.id.title);
        TextView amount = (TextView) newView.findViewById(R.id.amount);
        ImageView icon = (ImageView) newView.findViewById(R.id.icon);

        title.setText(instance.getTitle());
        amount.setText(instance.getAmount() + "");
        icon.setImageResource(R.drawable.blank);

        return newView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

