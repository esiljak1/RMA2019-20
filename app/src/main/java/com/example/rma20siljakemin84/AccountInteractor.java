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

public class AccountInteractor extends AsyncTask<String, Integer, Void> implements IAccountInteractor, GETAccountDetails.OnAccountSearchDone, POSTUpdateAccount.OnAccountUpdateDone {
    private AccountModel model = AccountModel.getInstance();
    private AccountPresenter presenter;

    public AccountModel getAccount(){
        return model;
    }

    public AccountInteractor() {
    }

    public void setAccount(AccountModel model) {
        this.model = model;
    }

    @Override
    protected Void doInBackground(String... strings) {
        if(strings.length > 0){
            try {
                double budget = Double.parseDouble(strings[0]);
                double totalLimit = Double.parseDouble(strings[1]);
                double monthLimit = Double.parseDouble(strings[2]);
                updateAccount(new AccountModel(0, budget, totalLimit, monthLimit));
                return null;
            } catch (IllegalAmountException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private void updateAccount(AccountModel account){
        String url1 = TransactionInteractor.ROOT + "/account/" + TransactionInteractor.API_KEY;

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

    @Override
    public AccountPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(AccountPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getAccountDetails() {
        new GETAccountDetails(this).execute();
    }

    @Override
    public void updateAccount(String budget, String totalLimit, String monthLimit) {
        new POSTUpdateAccount(this).execute(budget, totalLimit, monthLimit);
    }

    @Override
    public void onSearchDone(AccountModel account) {
        presenter.fetchedAccountDetails(account);
    }

    @Override
    public void onUpdateDone(AccountModel account) {
        presenter.fetchedAccountDetails(account);
    }
}
