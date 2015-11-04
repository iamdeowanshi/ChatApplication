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
import com.mtvindia.connect.util.PreferenceUtil;
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
    @Inject PreferenceUtil preferenceUtil;

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
        showProgress();
        socialAuth.login(SocialAuth.SocialType.FACEBOOK);
    }

    @OnClick(R.id.btn_gPlus)
    void gPlusLogin() {
        showProgress();
        socialAuth.login(SocialAuth.SocialType.GOOGLE);
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
        socialAuth.disconnect();
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        hideProgress();

        LoginRequest loginRequest = new LoginRequest(authResult);

        Timber.d("Success: " + authResult.getData());
        Timber.d("AccessToken: " + authResult.getAuthUser().getAccessToken());
        Timber.d("LoginRequest: " + gson.toJson(loginRequest));

        presenter.login(loginRequest);
    }

    @Override
    public void onCancel() {
        Timber.e("cancelled");
        toastShort("Cancelled");
        hideProgress();
    }

    @Override
    public void onError(Throwable throwable) {
        Timber.e(throwable, "Error");
        toastShort("Error: " + throwable);
        hideProgress();
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

        preferenceUtil.save(PreferenceUtil.USER, response);
        preferenceUtil.save(PreferenceUtil.IS_IN_REGISTRATION, isRegister);

        startActivity(NavigationActivity.class, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
