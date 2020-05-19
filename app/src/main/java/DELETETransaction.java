import android.os.AsyncTask;

import com.example.rma20siljakemin84.TransactionInteractor;

public class DELETETransaction extends AsyncTask<Integer, Integer, Void> {
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        String url1 = TransactionInteractor.ROOT + "/account/" + TransactionInteractor.API_KEY + "/transactions/" + integers[0];

        return null;
    }
}
