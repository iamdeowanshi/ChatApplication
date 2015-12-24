package com.mtvindia.connect.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.NetworkUtil;
import com.mtvindia.connect.util.UserPreference;
import com.mtvindia.connect.util.social.AuthResult;
import com.mtvindia.connect.util.social.SocialAuth;
import com.mtvindia.connect.util.social.SocialAuthCallback;

import javax.inject.Inject;

import butterknife.OnClick;
import timber.log.Timber;

public class LoginActivity extends BaseActivity implements SocialAuthCallback, LoginViewInteractor {

    @Inject LoginPresenter presenter;
    @Inject ChatListPresenter chatListPresenter;
    @Inject Gson gson;
    @Inject UserPreference userPreference;
    @Inject NetworkUtil networkUtil;
    @Inject DialogUtil dialogUtil;

    /*@Bind(R.id.progress_sign_in) ProgressBar progressSignIn;*/

    private ProgressDialog progressDialog;
    private SocialAuth socialAuth;
    private String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        injectDependencies();

        deviceToken = userPreference.readDeviceToken();

        socialAuth = new SocialAuth(this);
        socialAuth.setCallback(this);

        presenter.setViewInteractor(this);
    }

    @OnClick(R.id.btn_fb)
    void facebookLogin() {
        if( ! networkUtil.isOnline()) {
            displayInternetAlert();
        } else {
            showProgress();
            socialAuth.login(SocialAuth.SocialType.FACEBOOK );
        }
    }

    @OnClick(R.id.btn_gPlus)
    void gPlusLogin() {
        if( ! networkUtil.isOnline()) {
            displayInternetAlert();
        } else {
            showProgress();
            socialAuth.login(SocialAuth.SocialType.GOOGLE);
        }
    }

    void displayInternetAlert() {
        final AlertDialog alertDialog = (AlertDialog) dialogUtil.createAlertDialog(LoginActivity.this, "No Internet", "Seems like device is not connected to internet. Try again with active internet connection", "Exit", "Try Again");
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

    @Override
    public void onSuccess(AuthResult authResult) {
        hideProgress();

        LoginRequest loginRequest = new LoginRequest(authResult);
        loginRequest.setOsType(1);
        loginRequest.setCertificateType(0);
        loginRequest.setDeviceToken(deviceToken);

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

    @Override
    public void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Logging in");
        progressDialog.show();
        //progressSignIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
        //progressSignIn.setVisibility(View.GONE);
    }

    @Override
    public void loginDone(User response, boolean isRegister) {
        Timber.d(gson.toJson(response));

        userPreference.saveUser(response);
        userPreference.saveLoginStatus(isRegister);

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
    public void onBackPressed() {
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



}
