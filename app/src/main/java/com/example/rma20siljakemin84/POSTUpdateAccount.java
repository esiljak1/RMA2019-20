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

public class POSTUpdateAccount extends AsyncTask<String, Integer, Void> {
    public interface OnAccountUpdateDone{
        void onUpdateDone(AccountModel account);
    }

    private OnAccountUpdateDone oaud;
    private AccountModel account;

    public POSTUpdateAccount(OnAccountUpdateDone oaud) {
        this.oaud = oaud;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        oaud.onUpdateDone(account);
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            double budget = Double.parseDouble(strings[0]);
            double totalLimit = Double.parseDouble(strings[1]);
            double monthLimit = Double.parseDouble(strings[2]);
            account = new AccountModel(0, budget, totalLimit, monthLimit);
            updateAccount();
            return null;
        } catch (IllegalAmountException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateAccount(){
        String url1 = TransactionInteractor.getROOT() + "/account/" + TransactionInteractor.getApiKey();

        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = ((HttpURLConnection) url.openConnection());

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

            OutputStream os = urlConnection.getOutputStream();

            String jsonString = getJSONObject(account).toString();

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJSONObject(AccountModel account) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("budget", account.getBudget());
        json.put("totalLimit", account.getTotalLimit());
        json.put("monthLimit", account.getMonthLimit());

        return json;
    }
}
