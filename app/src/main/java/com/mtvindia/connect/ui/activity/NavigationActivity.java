package com.mtvindia.connect.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.data.model.Question;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.ui.fragment.AboutFragment;
import com.mtvindia.connect.ui.fragment.ChatFragment;
import com.mtvindia.connect.ui.fragment.NavigationDrawerFragment;
import com.mtvindia.connect.ui.fragment.PreferenceFragment;
import com.mtvindia.connect.ui.fragment.PrimaryQuestionFragment;
import com.mtvindia.connect.ui.fragment.ProfileFragment;
import com.mtvindia.connect.ui.fragment.ResultFragment;
import com.mtvindia.connect.ui.fragment.SecondaryQuestionFragment;
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.NetworkUtil;
import com.mtvindia.connect.util.QuestionPreference;
import com.mtvindia.connect.util.UserPreference;

import javax.inject.Inject;

import butterknife.Bind;
import timber.log.Timber;

public class NavigationActivity extends BaseActivity implements NavigationCallBack, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject NetworkUtil networkUtil;
    @Inject DialogUtil dialogUtil;

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @Bind(R.id.drawer)
    DrawerLayout drawerLayout;

    private NavigationDrawerFragment navigationDrawerFragment;
    private Fragment fragment;
    private User user;
    private LocationManager locationManager;

    final static int REQUEST_LOCATION = 1000;

    private GoogleApiClient googleApiClient;

    static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(internetReciever, filter);

        injectDependencies();

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        checkLocation();

        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.icon_logo);
        getSupportActionBar().setTitle(null);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragment.initDrawer(R.id.fragment_drawer, drawerLayout, toolbar);

        loadInitialItem();
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemSelected(NavigationItem item) {
        switch (item) {
            case FIND_PEOPLE:
                setDrawerEnabled(true);
                showFindMorePeople();
                break;
            case PROFILE:
                fragment = ProfileFragment.getInstance(null);
                addFragment(fragment);
                break;
            case PREFERENCE:
                fragment = PreferenceFragment.getInstance(null);
                addFragment(fragment);
                break;
            case CHAT:
                fragment = ChatFragment.getInstance(null);
                addFragment(fragment);
                break;
            case ABOUT:
                fragment = AboutFragment.getInstance(null);
                addFragment(fragment);
                break;
            case LOGOUT:
                startActivity(LoginActivity.class, null);
                userPreference.removeUser();
                finish();
                break;
        }
    }

    private void showFindMorePeople() {
        int count = questionPreference.readQuestionCount();
        Question question = questionPreference.readQuestionResponse();
        Fragment fragment;
        if(question == null) {
            fragment = PrimaryQuestionFragment.getInstance(null);
            addFragment(fragment);
        } else {
            if (!question.isAnswered()) {
                fragment = SecondaryQuestionFragment.getInstance(null);
                addFragment(fragment);
            } else if (count == 0) {
                fragment = PrimaryQuestionFragment.getInstance(null);
                addFragment(fragment);
            } else if (count > 0) {
                fragment = ResultFragment.getInstance(null);
                addFragment(fragment);
            }
        }
    }

    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }

    private void loadInitialItem() {
        boolean isInRegistration = userPreference.readLoginStatus();

        if(isInRegistration) {
            setDrawerEnabled(false);
            onItemSelected(NavigationItem.PREFERENCE);
        } else {
            onItemSelected(NavigationItem.FIND_PEOPLE);
        }
    }

    private void setDrawerEnabled(boolean isEnable) {
        navigationDrawerFragment.setActionBarDrawerToggleEnabled(isEnable);

        int drawerLockMode = isEnable ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;

        drawerLayout.setDrawerLockMode(drawerLockMode);
    }


    private final BroadcastReceiver internetReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean isOnline = networkUtil.isOnline();

            if(!isOnline) {
                dialogUtil.displayInternetAlert(NavigationActivity.this);
            }
        }
    };

    void checkLocation() {
        requestLocation();

        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        Timber.d("cancel", "no");
                        finish();
                        break;
                    }
                    case Activity.RESULT_OK:
                }
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void requestLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(NavigationActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_LOCATION);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        toastShort(connectionResult.toString());
        finish();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(this);
            locationManager = null;
        }catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (user == null) {
            return;
        }
        user = userPreference.readUser();
        user.setLatitude(location.getLatitude());
        user.setLongitude(location.getLongitude());
        userPreference.saveUser(user);
        try {
            locationManager.removeUpdates(this);
            locationManager = null;
        }catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetReciever);
        questionPreference.clearPreference();
    }
}
