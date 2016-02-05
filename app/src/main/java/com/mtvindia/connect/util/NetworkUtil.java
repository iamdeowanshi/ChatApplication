package com.mtvindia.connect.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.mtvindia.connect.app.di.Injector;

import javax.inject.Inject;

/**
 * Created by Sibi on 02/11/15.
 */

/**
 * Utility class that checks for internet connectivity.
 */
public class NetworkUtil {

    @Inject Context context;

    public NetworkUtil() {
        Injector.instance().inject(this);
    }

    /**
     * Checks for internet connectivity.
     * @return true if connected else false.
     */
    public Boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE );
        boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();

        return isConnected;
    }

}

