package com.example.rma20siljakemin84;

import android.os.AsyncTask;

public class POSTTransactionUpdate extends AsyncTask<String, Integer, Void> {
    public interface OnTransactionUpdateDone{
        void OnUpdateDone();
    }

    private OnTransactionUpdateDone otud;
    private TransactionModel transaction;

    public POSTTransactionUpdate(OnTransactionUpdateDone otud) {
        this.otud = otud;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(String... strings) {
        return null;
    }
}
