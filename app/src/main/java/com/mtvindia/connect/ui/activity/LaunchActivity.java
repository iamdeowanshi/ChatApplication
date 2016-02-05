package com.mtvindia.connect.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.util.Bakery;
import com.mtvindia.connect.util.PermissionUtil;
import com.mtvindia.connect.util.PreferenceUtil;
import com.mtvindia.connect.util.QuestionPreference;
import com.mtvindia.connect.util.UserPreference;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

public class LaunchActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject Bakery bakery;
    @Inject PermissionUtil permissionUtil;
    @Inject PreferenceUtil preference;

    private static int timeOut = 2000;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static int LOCATION_REQUEST_CODE = 1;

    private GoogleCloudMessaging gcm;
    private String regId;

    private static String[] LOCATION_PERMISSION = { Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();

        userPreference.removeMatchedUser();
        questionPreference.clearPreference();

        if (! preference.readBoolean(PreferenceUtil.FIRST_LAUNCH_DONE, false)) {
            preference.save(PreferenceUtil.FIRST_LAUNCH_DONE, true);
            startActivity(WalkThroughActivity.class, null);
            finish();
            return;
        }

        setContentView(R.layout.activity_launch);

        if (checkPlayServices()) {
            getRegId();
            checkLocation();
        } else {
            googleServicesError();
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissionUtil.verifyPermissions(grantResults)) {
                start();
            } else {
                bakery.snackShort(getContentView(), "Location permission were not granted");
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        finish();
                    }
                },200);

            }
        }
    }

    private void googleServicesError() {
        bakery.snackShort(getContentView(), "Google services are required for location and Google plus login");
    }

    /**
     * Checking for runtime permissions of location and accounts.
     */

    private void checkLocation() {
        if (ActivityCompat.checkSelfPermission(this, LOCATION_PERMISSION[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, LOCATION_PERMISSION[1]) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        } else {
            //If permission granted
            start();
        }
    }



    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION[1])) {
            Timber.d("request");
            // Display a SnackBar with an explanation and a button to trigger the request.
            bakery.snack(getContentView(), "Location permission are required to find Matches", Snackbar.LENGTH_INDEFINITE,  "Try Again", new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ActivityCompat.requestPermissions(LaunchActivity.this, LOCATION_PERMISSION, LOCATION_REQUEST_CODE);
                }
            });

        } else {
            // Permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, LOCATION_REQUEST_CODE);
        }
    }

    /**
     * Checking for the availability of Google play services
     *
     * @return false if playServices were not present.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Timber.i("Google Services", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Method to retrieve device token
     * and saving it to shared preferences.
     */
    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regId = gcm.register(getResources().getString(R.string.gcm_project_number));
                    msg = regId;
                } catch (IOException ex) {
                    msg = "Error : " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String regId) {
                if (regId.startsWith("Error")) {
                    getRegId();
                } else {
                    //gcm reg id is getting here.
                    Timber.d(regId);
                    userPreference.saveDeviceToken(regId);
                }
            }
        }.execute(null, null, null);
    }

    /**
     * If user details present login screen will be skipped
     * else login screen will be loaded
     */
    public void start() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
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
