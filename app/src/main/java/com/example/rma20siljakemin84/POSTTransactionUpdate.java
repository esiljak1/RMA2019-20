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
import java.util.Date;

public class POSTTransactionUpdate extends AsyncTask<String, Integer, Void> {
    public interface OnTransactionUpdateDone{
        void OnUpdateDone(TransactionModel transaction);
    }

    private OnTransactionUpdateDone otud;
    private TransactionModel transaction;

    public POSTTransactionUpdate(OnTransactionUpdateDone otud) {
        this.otud = otud;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        otud.OnUpdateDone(transaction);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String url1 = TransactionInteractor.ROOT + "/account/" + TransactionInteractor.API_KEY + "/transactions/" + strings[0];

        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = ((HttpURLConnection) url.openConnection());
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

            setUpTransaction(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7]);

            String jsonString = getJSONTransaction().toString();
            OutputStream os = urlConnection.getOutputStream();

            byte[] input = jsonString.getBytes("utf-8");
            os.write(input, 0, input.length);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while((responseLine = br.readLine()) != null){
                response.append(responseLine.trim());
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IllegalTransactionArgumentException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setUpTransaction(String id, String date, String title, String amount, String endDate, String itemDescription, String transactionInterval, String typeId) throws ParseException, IllegalTransactionArgumentException {
        transaction = new TransactionModel();

        transaction.setId(Integer.parseInt(id));
        Calendar temp = Calendar.getInstance();
        temp.setTime(getDate(date));
        transaction.setDate(temp);
        transaction.setTitle(title);
        transaction.setAmount(Double.parseDouble(amount));
        transaction.setType(Type.fromId(Integer.parseInt(typeId)));

        if(endDate != null && !endDate.trim().isEmpty()){
            Calendar tmp = Calendar.getInstance();
            tmp.setTime(getDate(endDate));
            transaction.setEndDate(tmp);
        }else{
            transaction.setEndDate(null);
        }

        if(itemDescription != null && !itemDescription.trim().isEmpty()){
            transaction.setItemDescription(itemDescription);
        }else{
            transaction.setItemDescription(null);
        }

        if(transactionInterval != null && !transactionInterval.trim().isEmpty()){
            transaction.setTransactionInterval(Integer.parseInt(transactionInterval));
        }else{
            transaction.setTransactionInterval(0);
        }
    }

    private Date getDate(String date) throws ParseException {
        return new SimpleDateFormat("dd.MM.yyyy").parse(date);
    }

    private JSONObject getJSONTransaction() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("date", getStringDate(transaction.getDate()));
        json.put("title", transaction.getTitle());
        json.put("amount", transaction.getAmount());
        json.put("TransactionTypeId", transaction.getType().getValue());


        if(transaction.getEndDate() != null){
            json.put("endDate", transaction.getEndDate());
        }
        if(transaction.getItemDescription() != null){
            json.put("itemDescription", transaction.getItemDescription());
        }
        if(transaction.getTransactionInterval() != 0){
            json.put("transactionInterval", transaction.getTransactionInterval());
        }
        return json;
    }

    private String getStringDate(Calendar date){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date.getTime());
    }
}
