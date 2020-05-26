package com.example.rma20siljakemin84;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GETAccountDetails extends AsyncTask<String, Integer, Void> {
    public interface OnAccountSearchDone{
        void onSearchDone(AccountModel account);
    }

    private OnAccountSearchDone oasd;
    private AccountModel account;

    public GETAccountDetails(OnAccountSearchDone oasd) {
        this.oasd = oasd;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        oasd.onSearchDone(account);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String url1 =   TransactionInteractor.getROOT() + "/account/" + TransactionInteractor.getApiKey();
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = ((HttpURLConnection) url.openConnection());
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = TransactionInteractor.convertStreamToString(in);

            JSONObject jo = new JSONObject(result);

            Integer id = jo.getInt("id");
            Double budget = jo.getDouble("budget");
            Double totalLimit = jo.getDouble("totalLimit");
            Double monthLimit = jo.getDouble("monthLimit");

            account = new AccountModel(id, budget, totalLimit, monthLimit);
        } catch (MalformedURLException e) {
            System.out.println("Izuzetak: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Izuzetak: " + e.getMessage());
        } catch (JSONException e) {
            System.out.println("Izuzetak: " + e.getMessage());
        } catch (IllegalAmountException e) {
            System.out.println("Izuzetak: " + e.getMessage());
        }
        return null;
    }
}
