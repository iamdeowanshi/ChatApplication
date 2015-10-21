package com.mtvindia.connect.ui.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.btn_fb)
    Button btnFb;
    @Bind(R.id.btn_gPlus)
    Button btnGplus;
    @Bind(R.id.progress_sign_in)
    ProgressBar progressSignIn;

    private CallbackManager callbackManager;

    private GoogleApiClient googleApiClient;
    private boolean isResolving = false;
    private boolean shouldResolve = false;
    private final String TAG = "Google Login";
    private static final int RC_SIGN_IN = 0;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        facebookApiLogin();

        setContentView(R.layout.activity_login);

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API).addScope(new Scope(Scopes.PROFILE)).build();
    }

    @OnClick(R.id.btn_fb)
    void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
    }

    @OnClick(R.id.btn_gPlus)
    void gPlusLogin() {
        progressSignIn.setVisibility(View.VISIBLE);
        shouldResolve = true;
        googleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                shouldResolve = false;
            }

            isResolving = false;
            googleApiClient.connect();
        }

        if (requestCode == REQ_SIGN_IN_REQUIRED && resultCode == RESULT_OK) {
            new RetrieveTokenTask().execute(account);
        }

        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    private void facebookApiLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,location,picture");
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

    @Override
    public void onConnected(Bundle bundle) {
        shouldResolve = false;

        try {
            account = Plus.AccountApi.getAccountName(googleApiClient);
            Log.d("Google ID\n", Plus.PeopleApi.getCurrentPerson(googleApiClient).getId());
            startActivity(NavigationActivity.class, null);
            toastShort("Welcome  " + Plus.PeopleApi.getCurrentPerson(googleApiClient).getDisplayName());
            new RetrieveTokenTask().execute(account);

        } catch (NullPointerException e) {
            Toast.makeText(this, "Could not reach your account :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!isResolving && shouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    isResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    isResolving = false;
                    googleApiClient.connect();
                }
            }
        }
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressSignIn.setVisibility(View.GONE);
            Log.d("Token Value: ", s);
        }

    }

}
