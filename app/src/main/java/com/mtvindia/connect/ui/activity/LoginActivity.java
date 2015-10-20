package com.mtvindia.connect.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.btn_fb)
    Button btnFb;
    @Bind(R.id.btn_gPlus)
    Button btnGplus;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        facebookLoginHandler();

        setContentView(R.layout.activity_login);
    }

    @OnClick(R.id.btn_fb)
    void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void facebookLoginHandler() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday,location");
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    toastLong("Welcome" + response.getJSONObject().getString("name"));
                                    startActivity(NavigationActivity.class, null);
                                } catch (JSONException e) {
                                }
                            }
                        });
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        toastShort("Login cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        toastShort(exception.getMessage());
                    }
                });
    }

}
