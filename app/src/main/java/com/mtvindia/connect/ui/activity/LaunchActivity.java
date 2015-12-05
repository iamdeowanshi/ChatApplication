package com.mtvindia.connect.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.util.QuestionPreference;
import com.mtvindia.connect.util.UserPreference;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

public class LaunchActivity extends BaseActivity {

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;

    private static int timeOut = 2000;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging gcm;

    private String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        injectDependencies();

        userPreference.removeMatchedUser();
        questionPreference.clearPreference();

        if (checkPlayServices()){
            getRegId();
        }

        proceed();
    }

    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Google Services", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    //getting reg id.
    public  void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(getResources().getString(R.string.gcm_project_number));
                    // msg = "Device registered, registration ID=" + regid;
                    msg =regid;
//                    Log.i("GCM regid",  msg);

                } catch (IOException ex) {
                    msg = "Error : " + ex.getMessage();
//                    Log.i("GCM Error",  msg);
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String regId) {
                if(regId.startsWith("Error"))
                {
                    getRegId();
                }
                else{
                    //gcm reg id is getting here.
                    Timber.d(regId);
                    userPreference.saveDeviceToken(regId);
                }
            }
        }.execute(null, null, null);
    }

    public void proceed() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userPreference.readUser() == null) {
                    startActivity(LoginActivity.class, null);
                } else {
                    startActivity(NavigationActivity.class, null);
                }
                finish();
            }
        }, timeOut);
    }

}
