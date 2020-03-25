package com.example.rma20siljakemin84;


import androidx.annotation.NonNull;

public enum Type {
    INDIVIDUALPAYMENT, REGULARPAYMENT, PURCHASE, INDIVIDUALINCOME, REGULARINCOME, Dummy;

    @NonNull
    @Override
    public String toString() {
        if(this.equals(Dummy)){
            return "Filter by";
        }
        return super.toString();
    }
}
