package com.mtvindia.connect.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.app.di.Injector;

import javax.inject.Inject;

/**
 * Created by Sibi on 02/11/15.
 */
public class NetworkUtil extends BaseActivity{

    @Inject Context context;

    public NetworkUtil() {
        Injector.instance().inject(this);
    }

    public Boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE );
        boolean isConnected = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();

        return isConnected;

    }

}

