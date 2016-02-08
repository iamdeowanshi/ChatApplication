package com.mtvindia.connect.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
import com.mtvindia.connect.data.model.NavigationItem;
import com.mtvindia.connect.data.model.Question;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.presenter.UpdatePresenter;
import com.mtvindia.connect.presenter.UpdateViewInteractor;
import com.mtvindia.connect.services.SmackService;
import com.mtvindia.connect.ui.callbacks.NavigationCallBack;
import com.mtvindia.connect.ui.fragment.AboutFragment;
import com.mtvindia.connect.ui.fragment.ChatListFragment;
import com.mtvindia.connect.ui.fragment.ChooseFragment;
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

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Aaditya Deowanshi
 *
 *         Navigation activity used to load different screen using navigation drawer.
 */

public class NavigationActivity extends BaseActivity implements NavigationCallBack, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, UpdateViewInteractor {

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject NetworkUtil networkUtil;
    @Inject DialogUtil dialogUtil;
    @Inject UpdatePresenter presenter;
    @Inject ChatListRepository chatListRepository;

    @Bind(R.id.toolbar_actionbar) Toolbar toolbar;
    @Bind(R.id.drawer) DrawerLayout drawerLayout;

    private final static int REQUEST_LOCATION = 1000;
    private static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private User user;
    private Fragment fragment;
    private boolean isInRegistration;
    private NavigationDrawerFragment navigationDrawerFragment;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        startService(new Intent(this, SmackService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(internetReciever, filter);

        injectDependencies();
        presenter.setViewInteractor(this);

        chatListRepository.reInitialize();

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        requestLocation();

        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.icon_logo);
        getSupportActionBar().setTitle(null);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragment.initDrawer(R.id.fragment_drawer, drawerLayout, toolbar);

        user = userPreference.readUser();

        loadInitialDrawerItem();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onItemSelected(NavigationItem.CHAT);
                return true;
            }
        });
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);

        if (isInRegistration) return false;

        if (chatListRepository.searchUser(user.getId()) != null) return true;

        return false;
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();

            return;
        }

        // Display popup, to confirm exit.
        final AlertDialog alertDialog = (AlertDialog) dialogUtil.createAlertDialog(NavigationActivity.this, "Exit", "Do you want to exit", "Yes", "No");
        alertDialog.show();
        alertDialog.setCancelable(true);
        Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        unregisterReceiver(internetReciever);
        questionPreference.clearPreference();
        userPreference.removeMatchedUser();
    }

    @Override
    public void onItemSelected(NavigationItem item) {
        switch (item) {
            case FIND_PEOPLE:
                setDrawerEnabled(true);
                viewForFindMorePeople();
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
                fragment = ChatListFragment.getInstance(null);
                addFragment(fragment);
                break;
            case ABOUT:
                fragment = AboutFragment.getInstance(null);
                addFragment(fragment);
                break;
            case LOGOUT:
                startActivity(LoginActivity.class, null);
                userPreference.removeUser();
                disconnectChatServer();
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case Activity.RESULT_CANCELED: {
                requestLocation();
                break;
            }
            case Activity.RESULT_OK:
                requestLocation();
                break;
        }
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
        requestLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void showProgress() {
    }

    @Override
    public void hideProgress() {
    }

    @Override
    public void updateDone(User user) {
        userPreference.saveUser(user);
    }

    @Override
    public void onError(Throwable throwable) {
    }

    /**
     * Receiver to check for internet connectivity.
     */
    private final BroadcastReceiver internetReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Boolean isOnline = networkUtil.isOnline();

            if (!isOnline) {
                final AlertDialog alertDialog = (AlertDialog) dialogUtil.createAlertDialog(NavigationActivity.this, "No Internet", "Seems like device is not connected to internet. Try again with active internet connection", "Exit", "Try Again");
                alertDialog.show();
                Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!networkUtil.isOnline()) {
                            dialogUtil.createAlertDialog(NavigationActivity.this, "No Internet Connectivity", "Try again with active internet connection", "Exit", "Try Again");
                        } else {
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        }
    };

    /**
     * Disconnect to chat server.
     */
    private void disconnectChatServer() {
        Intent intent = new Intent(this, SmackService.class);
        this.stopService(intent);
    }

    /**
     * Requesting for location access.
     */
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
                        getlastKnownLocation();
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

    /**
     * Depending upon number of question answered, toggle between question, answer or choose match view on click of find more people.
     */
    private void viewForFindMorePeople() {
        int count = questionPreference.readQuestionCount();
        Question question = questionPreference.readQuestionResponse();
        List<User> matchedUser = userPreference.readMatchedUser();
        Fragment fragment;

        if (question == null) {
            fragment = PrimaryQuestionFragment.getInstance(null);
            addFragment(fragment);

            return;
        }

        if (count == 0) {
            fragment = PrimaryQuestionFragment.getInstance(null);
            addFragment(fragment);

            return;
        }

        if (!question.isAnswered()) {
            fragment = SecondaryQuestionFragment.getInstance(null);
            addFragment(fragment);

            return;
        }

        if (matchedUser.size() != 0) {
            fragment = ChooseFragment.getInstance(null);
            addFragment(fragment);

            return;
        }

        if (count > 0) {
            fragment = ResultFragment.getInstance(null);
            addFragment(fragment);

            return;
        }
    }

    /**
     * Method to add fragment to container.
     *
     * @param fragment
     */
    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }

    /**
     * Loads initial tab from navigation drawer.
     */
    private void loadInitialDrawerItem() {
        isInRegistration = userPreference.readLoginStatus();

        if (isInRegistration) {
            setDrawerEnabled(false);
            onItemSelected(NavigationItem.PREFERENCE);

            return;
        }

        if (chatListRepository.searchUser(user.getId()) != null) {
            onItemSelected(NavigationItem.CHAT);

            return;
        }

        onItemSelected(NavigationItem.FIND_PEOPLE);
    }

    /**
     * If registration process, disable navigation drawer so that user completes registration.
     *
     * @param isEnable
     */
    private void setDrawerEnabled(boolean isEnable) {
        navigationDrawerFragment.setActionBarDrawerToggleEnabled(isEnable);

        int drawerLockMode = isEnable ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;

        drawerLayout.setDrawerLockMode(drawerLockMode);
    }

    /**
     * Returns last known location.
     */
    private void getlastKnownLocation() {
        LocationRequest locationRequest = new LocationRequest();
        com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Location userLocation = new Location("point A");
                    userLocation.setLatitude(user.getLatitude());
                    userLocation.setLongitude(user.getLongitude());
                    Location currentLocation = new Location("point B");
                    currentLocation.setLatitude(latitude);
                    currentLocation.setLongitude(longitude);
                    double distance = userLocation.distanceTo(currentLocation);
                    if (distance > 50000) {
                        user.setLatitude(location.getLatitude());
                        user.setLongitude(location.getLongitude());
                        userPreference.saveUser(user);
                        presenter.updateLocation(user);
                    }
                }

            }
        };
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
    }

}
