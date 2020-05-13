package com.example.rma20siljakemin84;


import androidx.annotation.NonNull;

public enum Type {
    INDIVIDUALPAYMENT(5),
    REGULARPAYMENT(1),
    PURCHASE(3),
    INDIVIDUALINCOME(4),
    REGULARINCOME(2),
    Dummy(0); //Dummy dodan da se Filer by moze ispisati u spinneru

    private int value;
    Type(int value){
        this.value = value;
    }

    public static Type fromId(int id){
        for(Type type : values()){
            if(type.value == id){
                return type;
            }
        }return Dummy;
    }

    @NonNull
    @Override
    public String toString() {
        if(this.equals(Dummy)){
            return "Filter by";
        }
        return super.toString();
    }
}
