package com.example.rma20siljakemin84;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TransactionInteractor extends AsyncTask<String, Integer, Void> implements ITransactionInteractor {
    public interface OnTransactionSearchDone{
        void onDone(ArrayList<TransactionModel> result, boolean isPost);
    }
    public static final String ROOT = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com";
    public static final String API_KEY = "6e8e09ce-5c99-4f3d-a9bd-d0d60b65a5d3";
    private TransactionModel model = new TransactionModel();
    private ArrayList<TransactionModel> transactions = new ArrayList<>();
    private OnTransactionSearchDone osd;
    private boolean isPost = false;

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
        osd.onDone(transactions, isPost);
    }

    @Override
    protected Void doInBackground(String... strings) {
        if(strings.length == 4){
            isPost = false;
            getFilteredTransactions(strings[0], strings[1], strings[2], strings[3]);
            return null;
        }
        else if(strings.length == 7){
            isPost = true;
            Calendar date = Calendar.getInstance(), endDate = null;
            try {
                date.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(strings[0]));
                if(!strings[3].equals("")){
                    endDate = Calendar.getInstance();
                    endDate.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(strings[3]));
                }
                Double amount = Double.parseDouble(strings[2]);
                int typeId = Integer.parseInt(strings[6]);

                String itemDescription = null;
                if(strings[4] != ""){
                    itemDescription = strings[4];
                }

                int transactionInterval = 0;
                if(!strings[5].equals("")){
                    transactionInterval = Integer.parseInt(strings[5]);
                }

                addNewTransaction(new TransactionModel(date, amount, strings[1], Type.fromId(typeId), itemDescription, transactionInterval, endDate));
            } catch (ParseException e) {
                e.printStackTrace();
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

    private void getFilteredTransactions(String transactionTypeId, String sort, String month, String year){
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

    private void addNewTransaction(TransactionModel transaction){
        String url1 = ROOT + "/account/" + API_KEY + "/transactions";

        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = ((HttpURLConnection) url.openConnection());
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
            writer.write(getPostDataString(putParametersInMap(transaction)));

            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> putParametersInMap(TransactionModel transaction){
        HashMap<String, String> ret = new HashMap<>();

        ret.put("date", new SimpleDateFormat("yyyy-MM-dd").format(transaction.getDate().getTime()));
        ret.put("title", transaction.getTitle());
        ret.put("amount", transaction.getAmount() + "");
        if(transaction.getEndDate() != null){
            ret.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(transaction.getEndDate().getTime()));
        }
        if(transaction.getItemDescription() != null){
            ret.put("itemDescription", transaction.getItemDescription());
        }
        if(transaction.getTransactionInterval() != 0){
            ret.put("transactionInterval", transaction.getTransactionInterval() + "");
        }
        ret.put("typeId", transaction.getType().getValue() + "");

        return ret;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for(Map.Entry<String, String> entry : params.entrySet()){
            if(first){
                first = false;
            }else{
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), "utf-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "utf-8"));
        }

        return result.toString();
    }
}
