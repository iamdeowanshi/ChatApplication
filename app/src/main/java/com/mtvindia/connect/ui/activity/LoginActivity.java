package com.mtvindia.connect.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.data.model.LoginRequest;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.ChatListPresenter;
import com.mtvindia.connect.presenter.LoginPresenter;
import com.mtvindia.connect.presenter.LoginViewInteractor;
import com.mtvindia.connect.util.Bakery;
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.NetworkUtil;
import com.mtvindia.connect.util.PermissionUtil;
import com.mtvindia.connect.util.UserPreference;
import com.mtvindia.connect.util.social.AuthResult;
import com.mtvindia.connect.util.social.SocialAuth;
import com.mtvindia.connect.util.social.SocialAuthCallback;
import com.onesignal.OneSignal;

import javax.inject.Inject;

import butterknife.OnClick;
import timber.log.Timber;

public class LoginActivity extends BaseActivity implements SocialAuthCallback, LoginViewInteractor, ActivityCompat.OnRequestPermissionsResultCallback {

    @Inject LoginPresenter presenter;
    @Inject ChatListPresenter chatListPresenter;
    @Inject Gson gson;
    @Inject UserPreference userPreference;
    @Inject NetworkUtil networkUtil;
    @Inject DialogUtil dialogUtil;
    @Inject PermissionUtil permissionUtil;
    @Inject Bakery bakery;

    private final static int ACCOUNT_REQUEST_CODE = 0;

    private static String[] ACCOUNT_PERMISSION = { Manifest.permission.GET_ACCOUNTS};

    private ProgressDialog progressDialog;
    private SocialAuth socialAuth;
    private String deviceToken;
    private String playerId;
    private int button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        injectDependencies();

        deviceToken = userPreference.readDeviceToken();

        socialAuth = new SocialAuth(this);
        socialAuth.setCallback(this);

        setPlayerId();

        presenter.setViewInteractor(this);
    }

    /**
     * Facebook login button.
     */
    @OnClick(R.id.btn_fb)
    void facebookLogin() {
        if( ! networkUtil.isOnline()) {
            displayInternetAlert();
        } else {
            button = R.id.btn_fb;
            login();
        }
    }

    /**
     * Google Plus login button.
     */
    @OnClick(R.id.btn_gPlus)
    void gPlusLogin() {
        if( ! networkUtil.isOnline()) {
            displayInternetAlert();
        } else {
            button = R.id.btn_gPlus;
            checkAccount();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialAuth.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * After successful login on facebook or google.
     * @param authResult
     */
    @Override
    public void onSuccess(AuthResult authResult) {
        hideProgress();

        LoginRequest loginRequest = new LoginRequest(authResult);
        loginRequest.setOsType(1);
        loginRequest.setCertificateType(0);
        loginRequest.setDeviceToken(deviceToken);
        loginRequest.setPlayerId(playerId);

        // making api call to server for login.
        presenter.login(loginRequest);
    }

    @Override
    public void onCancel() {
        hideProgress();
    }

    @Override
    public void onError(Throwable throwable) {
        hideProgress();
        toastShort("Some error on Login");
        Timber.e(throwable, "Error");
    }

    /**
     * Progress Dialog to show login process.
     */
    @Override
    public void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Logging in");
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    /**
     * Checking for registration and
     * saving user details to shared preferences.
     * @param response contains the user details.
     * @param isRegister will be false if user is present otherwise true for new User registration.
     */
    @Override
    public void loginDone(User response, boolean isRegister) {
        Timber.d(gson.toJson(response));

        userPreference.saveUser(response);
        userPreference.saveLoginStatus(isRegister);

        // Checking for chatList in database.
        chatListPresenter.getChatUsers(userPreference.readUser().getAuthHeader());

        startActivity(NavigationActivity.class, null);
        socialAuth.disconnect();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ACCOUNT_REQUEST_CODE) {
            if (permissionUtil.verifyPermissions(grantResults)) {
                login();
            } else {
                /*bakery.snack(getContentView(), "Contact permission are required for Login", Snackbar.LENGTH_INDEFINITE, "Try Again", new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        ActivityCompat.requestPermissions(LoginActivity.this, ACCOUNT_PERMISSION, ACCOUNT_REQUEST_CODE);
                    }
                });*/
                bakery.snackShort(getContentView(), "Permissions were not granted");
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Dialog to confirm exit.
        final AlertDialog alertDialog = (AlertDialog) dialogUtil.createAlertDialog(this, "Exit", "Do you want to exit", "Yes", "No");
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
                alertDialog.dismiss();
            }
        });
    }

    private void checkAccount() {
        if (ActivityCompat.checkSelfPermission(this, ACCOUNT_PERMISSION[0]) != PackageManager.PERMISSION_GRANTED) {
            requestAccountPermission();
        } else {
            login();
        }
    }

    private void requestAccountPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCOUNT_PERMISSION[0])) {
            bakery.snack(getContentView(), "Contact permission are required for Login", Snackbar.LENGTH_INDEFINITE,  "Try Again", new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ActivityCompat.requestPermissions(LoginActivity.this, ACCOUNT_PERMISSION, ACCOUNT_REQUEST_CODE);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, ACCOUNT_PERMISSION, ACCOUNT_REQUEST_CODE);
        }
    }

    /**
     * Fetching playersId from OneSignal Server.
     */
    private void setPlayerId() {
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                playerId = userId;
                Log.d("debug", "User:" + userId);
                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);
            }
        });
    }

    /**
     * No internet dialog.
     */
    private void displayInternetAlert() {
        final AlertDialog alertDialog = (AlertDialog) dialogUtil.createAlertDialog(LoginActivity.this,
                "No Internet", "Seems like device is not connected to internet. Try again with active internet connection", "Exit", "Try Again");
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
                if(!networkUtil.isOnline()) {
                    dialogUtil.createAlertDialog(LoginActivity.this, "No Internet", "Seems like device is not connected to internet. Try again with active internet connection", "Exit", "Try Again");
                } else {
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void login() {
        showProgress();
        switch (button) {
            case R.id.btn_fb :
                socialAuth.login(SocialAuth.SocialType.FACEBOOK );
                break;
            case R.id.btn_gPlus :
                socialAuth.login(SocialAuth.SocialType.GOOGLE );
                break;
        }
    }

}
