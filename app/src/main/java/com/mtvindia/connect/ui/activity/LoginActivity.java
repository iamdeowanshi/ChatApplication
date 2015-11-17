package com.mtvindia.connect.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.data.model.LoginRequest;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.LoginPresenter;
import com.mtvindia.connect.presenter.LoginViewInteractor;
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.NetworkUtil;
import com.mtvindia.connect.util.UserPreference;
import com.mtvindia.connect.util.social.AuthResult;
import com.mtvindia.connect.util.social.SocialAuth;
import com.mtvindia.connect.util.social.SocialAuthCallback;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

public class LoginActivity extends BaseActivity implements SocialAuthCallback, LoginViewInteractor {

    @Inject LoginPresenter presenter;
    @Inject Gson gson;
    @Inject UserPreference userPreference;
    @Inject NetworkUtil networkUtil;
    @Inject
    DialogUtil dialogUtil;

    @Bind(R.id.progress_sign_in) ProgressBar progressSignIn;

    private SocialAuth socialAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        injectDependencies();

        socialAuth = new SocialAuth(this);
        socialAuth.setCallback(this);

        presenter.setViewInteractor(this);
    }

    @OnClick(R.id.btn_fb)
    void facebookLogin() {
        if( ! networkUtil.isOnline()) {
            dialogUtil.displayInternetAlert(LoginActivity.this);
        } else {
            showProgress();
            socialAuth.login(SocialAuth.SocialType.FACEBOOK );
        }
    }

    @OnClick(R.id.btn_gPlus)
    void gPlusLogin() {
        if( ! networkUtil.isOnline()) {
            dialogUtil.displayInternetAlert(LoginActivity.this);
        } else {
            showProgress();
            socialAuth.login(SocialAuth.SocialType.GOOGLE);
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

    @Override
    public void onSuccess(AuthResult authResult) {
        hideProgress();

        LoginRequest loginRequest = new LoginRequest(authResult);
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
        progressSignIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressSignIn.setVisibility(View.GONE);
    }

    @Override
    public void loginDone(User response, boolean isRegister) {
        Timber.d(gson.toJson(response));

        userPreference.saveUser(response);

        User user = userPreference.readUser();

        userPreference.saveLoginStatus(isRegister);

        startActivity(NavigationActivity.class, null);
        socialAuth.disconnect();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
