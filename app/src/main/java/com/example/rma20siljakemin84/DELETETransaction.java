package com.example.rma20siljakemin84;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DELETETransaction extends AsyncTask<Integer, Integer, Void> {
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        String url1 = TransactionInteractor.ROOT + "/account/" + TransactionInteractor.API_KEY + "/transactions/" + integers[0];

        try {

            URL url = new URL(url1);
            HttpURLConnection urlConnection = ((HttpURLConnection) url.openConnection());
            urlConnection.setRequestMethod("DELETE");

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while((responseLine = br.readLine()) != null){
                response.append(responseLine.trim());
            }

            System.out.println("Odgovor: " + response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

