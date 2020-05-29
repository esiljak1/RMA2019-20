package com.example.rma20siljakemin84;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if(cm.getActiveNetworkInfo() == null){
            Toast.makeText(context, "Disconected", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
        }
    }
}
