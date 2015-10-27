package com.mtvindia.connect.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.util.social.AuthResult;
import com.mtvindia.connect.util.social.SocialAuth;
import com.mtvindia.connect.util.social.SocialAuthCallback;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

public class LoginActivitySocial extends BaseActivity implements SocialAuthCallback {

    @Bind(R.id.btn_fb)
    Button btnFb;
    @Bind(R.id.btn_gPlus)
    Button btnGplus;
    @Bind(R.id.progress_sign_in)
    ProgressBar progressSignIn;

    private final String TAG = "Google Login";
    private User user = new User();

    SocialAuth socialAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        socialAuth = new SocialAuth(this);
        socialAuth.setCallback(this);

    }

    @OnClick(R.id.btn_fb)
    void facebookLogin() {
        socialAuth.login(SocialAuth.SocialType.FACEBOOK);
    }

    @OnClick(R.id.btn_gPlus)
    void gPlusLogin() {
        progressSignIn.setVisibility(View.VISIBLE);
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
        user.setEmail(authResult.getAuthUser().getEmail());
        user.setFirstName(authResult.getAuthUser().getFirstName());
        user.setGender(authResult.getAuthUser().getGender());
        user.setSocialId(authResult.getAuthUser().getSocialId());
        user.setLastName(authResult.getAuthUser().getLastName());
        user.setProfilePic(authResult.getAuthUser().getProfilePic());
        user.setAccessToken(authResult.getAuthUser().getAccessToken());

        Timber.d("Success: " + authResult.getData());
        Timber.d("AccessToken: " + authResult.getAuthUser().getAccessToken());
        progressSignIn.setVisibility(View.INVISIBLE);
        startActivity(NavigationActivity.class, null);
    }

    @Override
    public void onCancel() {
        Timber.e("cancelled");
    }

    @Override
    public void onError(Throwable throwable) {
        Timber.e(throwable, "Error");
    }

}
