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

public class TypeListAdapter extends ArrayAdapter<Type> {
    private int resource;

    public TypeListAdapter(@NonNull Context context, int resource, @NonNull List<Type> objects) {
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
        Type type = getItem(position);

        TextView textType = (TextView) newView.findViewById(R.id.textView5);
        ImageView img = (ImageView) newView.findViewById(R.id.img);

        textType.setText(type.toString());
        if(!type.equals(Type.Dummy)){
            if(type.equals(Type.REGULARPAYMENT)){
                img.setImageResource(R.drawable.regular_payment);
            }else if(type.equals(Type.REGULARINCOME)){
                img.setImageResource(R.drawable.regular_income);
            }else if(type.equals(Type.PURCHASE)){
                img.setImageResource(R.drawable.purchase);
            }else if(type.equals(Type.INDIVIDUALINCOME)){
                img.setImageResource(R.drawable.individual_income);
            }else{
                img.setImageResource(R.drawable.individual_payment);
            }
        }

        return newView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
