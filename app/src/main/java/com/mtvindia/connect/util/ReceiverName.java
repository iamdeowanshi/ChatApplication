package com.mtvindia.connect.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import timber.log.Timber;

/**
 * Created by Sibi on 02/11/15.
 */
public class ReceiverName extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null)
            return;
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            Timber.d("Internet", "connected");
        } else {
            /*final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Add Photo!");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.show();
*/
            Toast.makeText(context, "No Internet Access", Toast.LENGTH_SHORT).show();
        }
    }

}

