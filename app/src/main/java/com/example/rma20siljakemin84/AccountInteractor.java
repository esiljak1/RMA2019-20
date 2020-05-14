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

public class AccountInteractor extends AsyncTask<String, Integer, Void> implements IAccountInteractor {

    public interface OnAccountSearchDone{
        void onDone(AccountModel account);
    }
    private AccountModel model = AccountModel.getInstance();
    private OnAccountSearchDone oad;

    public AccountModel getAccount(){
        return model;
    }

    public AccountInteractor(OnAccountSearchDone oad) {
        this.oad = oad;
    }

    public AccountInteractor() {
    }

    public void setAccount(AccountModel model) {
        this.model = model;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String url1 =   TransactionInteractor.ROOT + "/account/" + TransactionInteractor.API_KEY;
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

            model = new AccountModel(id, budget, totalLimit, monthLimit);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        oad.onDone(model);
    }
}
