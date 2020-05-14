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

public class TransactionInteractor extends AsyncTask<String, Integer, Void> implements ITransactionInteractor {
    public interface OnTransactionSearchDone{
        void onDone(ArrayList<TransactionModel> result);
    }
    public static final String ROOT = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com";
    public static final String API_KEY = "6e8e09ce-5c99-4f3d-a9bd-d0d60b65a5d3";
    private TransactionModel model = new TransactionModel();
    private ArrayList<TransactionModel> transactions = new ArrayList<>();
    private OnTransactionSearchDone osd;

    public TransactionInteractor(OnTransactionSearchDone osd) {
        this.osd = osd;
    }

    public TransactionInteractor(){

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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        osd.onDone(transactions);
    }

    @Override
    protected Void doInBackground(String... strings) {
        if(strings.length == 4){
            filterTransactions(strings[0], strings[1], strings[2], strings[3]);
            return null;
        }
        String url1 = ROOT + "/account/" + API_KEY + "/transactions?page=";
        ArrayList<TransactionModel> list = new ArrayList<>(transactions);
        transactions.clear();
        for(int i = 0; i < 4; i++){
            String temp = url1 + i;
            try {
                URL url = new URL(temp);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String rezultat = convertStreamToString(in);

                JSONObject jo = new JSONObject(rezultat);
                JSONArray results = jo.getJSONArray("transactions");

                for(int j = 0; j < 5; j++){
                    JSONObject transaction = results.getJSONObject(j);
                    transactions.add(getTransactionFromJSON(transaction));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                transactions = new ArrayList<>(list);
            } catch (IOException e) {
                e.printStackTrace();
                transactions = new ArrayList<>(list);
            } catch (JSONException e) {
                e.printStackTrace();
                transactions = new ArrayList<>(list);
            } catch (ParseException e) {
                e.printStackTrace();
                transactions = new ArrayList<>(list);
            }
        }
        return null;
    }

    public ArrayList<TransactionModel> getTransactions(){
        return transactions;
    }

    public void update(TransactionModel newTransaction){
        transactions.remove(newTransaction);
        transactions.add(newTransaction);
    }
    public void delete(TransactionModel transaction){
        model.removeTransaction(transaction);
    }

    @Override
    public void setTransactions(ArrayList<TransactionModel> transactions) {
        this.transactions = transactions;
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
        if(!tInterval.equals("null")){
            transactionInterval = Integer.parseInt(tInterval);
        }
        String eDate = json.getString("endDate");
        Calendar endDate = null;
        Integer type_id = json.getInt("TransactionTypeId");
        Type type = null;
        if(type_id != null){
            type = Type.fromId(type_id);
        }
        if(!eDate.equals("null")){
            endDate = Calendar.getInstance();
            endDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(eDate));
        }
        TransactionModel transaction = new TransactionModel(id, date, amount, title, type, itemDescription, transactionInterval, endDate);
        return transaction;
    }

    private void filterTransactions(String transactionTypeId, String sort, String month, String year){
        String url1 = ROOT + "/account/" + API_KEY + "/transactions/filter?page=";
        ArrayList<TransactionModel> before = new ArrayList<>(transactions);
        transactions.clear();
        int i = 0;
        while(true){
            String temp = url1 + i;
            temp += setParameters(transactionTypeId, sort, month, year);

            try {
                URL url = new URL(temp);
                HttpURLConnection urlConnection = ((HttpURLConnection) url.openConnection());
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
}
