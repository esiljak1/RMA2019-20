package com.example.rma20siljakemin84;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GETFilteredTransactions extends AsyncTask<String, Integer, Void> {
    public interface OnTransactionSearchDone{
        void onGetDone(ArrayList<TransactionModel> transactions);
    }
    private OnTransactionSearchDone ots;
    private ArrayList<TransactionModel> transactions = new ArrayList<>();

    public GETFilteredTransactions(OnTransactionSearchDone ots) {
        this.ots = ots;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ots.onGetDone(transactions);
    }

    @Override
    protected Void doInBackground(String... strings) {
        getFilteredTransactions(strings[0], strings[1], strings[2], strings[3]);
        return null;
    }

    public static String convertStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }

    private String setParameter(String name, String parameter){
        String ret = "";
        if(parameter != null && !parameter.trim().isEmpty()){
            ret += "&" + name + "=" + parameter;
        }
        return ret;
    }

    private String setParameters(String transactionTypeId, String sort, String month, String year){
        String ret = "";
        ret += setParameter("typeId", transactionTypeId);
        ret += setParameter("sort", sort);
        ret += setParameter("month", month);
        ret += setParameter("year", year);
        return ret;
    }

    private void getFilteredTransactions(String transactionTypeId, String sort, String month, String year){
        String url1 = TransactionInteractor.getROOT() + "/account/" + TransactionInteractor.getApiKey() + "/transactions/filter/?page=";
        ArrayList<TransactionModel> before = new ArrayList<>(transactions);
        transactions.clear();
        int i = 0;
        while(true){
            String temp = url1 + i;
            temp += setParameters(transactionTypeId, sort, month, year);

            try {
                URL url = new URL(temp);
                HttpURLConnection urlConnection = ((HttpURLConnection) url.openConnection());
                urlConnection.setRequestMethod("GET");
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());

                String result = convertStreamToString(is);

                JSONObject jo = new JSONObject(result);
                JSONArray array = jo.getJSONArray("transactions");
                if(array.length() == 0) break;

                for(int j = 0; j < array.length(); j++){
                    JSONObject transaction = array.getJSONObject(j);
                    transactions.add(getTransactionFromJSON(transaction));
                }
                i++;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                transactions = new ArrayList<>(before);
            } catch (IOException e) {
                e.printStackTrace();
                transactions = new ArrayList<>(before);
            } catch (JSONException e) {
                e.printStackTrace();
                transactions = new ArrayList<>(before);
            } catch (ParseException e) {
                e.printStackTrace();
                transactions = new ArrayList<>(before);
            }
        }
    }

    private TransactionModel getTransactionFromJSON(JSONObject json) throws JSONException, ParseException {
        Integer id = json.getInt("id");
        Calendar date = Calendar.getInstance();
        date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(json.getString("date")));
        String title = json.getString("title");
        Double amount = json.getDouble("amount");
        String itemDescription = json.getString("itemDescription");
        String tInterval = null;
        tInterval = json.getString("transactionInterval");
        Integer transactionInterval = 0;
        if(tInterval != null && !tInterval.equals("null")){
            transactionInterval = Integer.parseInt(tInterval);
        }
        String eDate = json.getString("endDate");
        Calendar endDate = null;
        Integer type_id = json.getInt("TransactionTypeId");
        Type type = null;
        if(type_id != null){
            type = Type.fromId(type_id);
        }
        if(eDate != null && !eDate.equals("null")){
            endDate = Calendar.getInstance();
            endDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(eDate));
        }
        TransactionModel transaction = new TransactionModel(id, date, amount, title, type, itemDescription, transactionInterval, endDate);
        return transaction;
    }
}
