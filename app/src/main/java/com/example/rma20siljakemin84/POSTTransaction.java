package com.example.rma20siljakemin84;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class POSTTransaction extends AsyncTask<String, Integer, Void> {
    public interface OnTransactionPostDone{
        void onPostDone(TransactionModel transaction);
    }

    private OnTransactionPostDone otp;
    private TransactionModel transaction;

    public POSTTransaction(OnTransactionPostDone otp) {
        this.otp = otp;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        otp.onPostDone(transaction);
    }

    @Override
    protected Void doInBackground(String... strings) {
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
        return null;
    }

    private void addNewTransaction(TransactionModel transaction){
        String url1 = TransactionInteractor.ROOT + "/account/" + TransactionInteractor.API_KEY + "/transactions";

        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = ((HttpURLConnection) url.openConnection());
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

            OutputStream os = urlConnection.getOutputStream();

            String jsonString = getJSONObject(transaction).toString();
            byte[] input = jsonString.getBytes("utf-8");
            os.write(input, 0, input.length);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while((responseLine = br.readLine()) != null){
                response.append(responseLine.trim());
            }
            getAddedTransaction(response.toString());

        } catch (MalformedURLException e) {
            System.out.println("Izuzetak: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Izuzetak: " + e.getMessage());
        } catch (JSONException e) {
            System.out.println("Izuzetak: " + e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJSONObject(TransactionModel transaction) throws JSONException {
        JSONObject ret = new JSONObject();

        ret.put("date", getStringDate(transaction.getDate()));
        ret.put("title", transaction.getTitle());
        ret.put("amount", transaction.getAmount());
        if(transaction.getEndDate() != null){
            ret.put("endDate", getStringDate(transaction.getEndDate()));
        }
        if(transaction.getItemDescription() != null){
            ret.put("itemDescription", transaction.getItemDescription());
        }
        if(transaction.getTransactionInterval() != 0){
            ret.put("transactionInterval", transaction.getTransactionInterval());
        }
        ret.put("typeId", transaction.getType().getValue());

        return ret;
    }

    private String getStringDate(Calendar cal){
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    private void getAddedTransaction(String string) throws JSONException, ParseException {
        JSONObject json = new JSONObject(string);

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

        transaction = new TransactionModel(id, date, amount, title, type, itemDescription, transactionInterval, endDate);
    }
}
