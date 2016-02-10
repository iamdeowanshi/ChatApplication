package com.mtvindia.connect.util.social;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;

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

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import timber.log.Timber;

/**
 * @author Aaditya Deowanshi
 *
 *         Socail Authentication class.
 */

public class SocialAuth implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public enum SocialType {
        GOOGLE,
        FACEBOOK
    }

    private static final int RC_SIGN_IN = 0;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;

    private boolean isResolving = false;
    private boolean shouldResolve = false;
    private GoogleApiClient googleApiClient;
    private CallbackManager fbCallbackManager;

    private Activity activity;
    private SocialType socialType;
    private SocialAuthCallback callback;

    public SocialAuth(Activity activity) {
        this.activity = activity;
        initGoogleClient();
        initFacebookClient();
    }

    // Google Api callbacks
    @Override
    public void onConnected(Bundle bundle) {
        shouldResolve = false;
        try {
            new RetrieveTokenTask(Plus.PeopleApi.getCurrentPerson(googleApiClient).toString(), Plus.AccountApi.getAccountName(googleApiClient))
                    .execute(Plus.AccountApi.getAccountName(googleApiClient));
        } catch (NullPointerException e) {
            callback.onError(e);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (isResolving && shouldResolve) return;

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(activity, RC_SIGN_IN);
                isResolving = true;
            } catch (IntentSender.SendIntentException e) {
                isResolving = false;
                callback.onError(e);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                shouldResolve = false;
            }

            isResolving = false;
            googleApiClient.connect();
        }

        if (requestCode == REQ_SIGN_IN_REQUIRED && resultCode == Activity.RESULT_OK) {
            // Retrieving access token after sign in.
            new RetrieveTokenTask(Plus.PeopleApi.getCurrentPerson(googleApiClient).toString(), Plus.AccountApi.getAccountName(googleApiClient))
                    .execute(Plus.AccountApi.getAccountName(googleApiClient));
        }

        if (fbCallbackManager.onActivityResult(requestCode, resultCode, data)) return;
    }

    /**
     * Initializing Google.
     */
    private void initGoogleClient() {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    /**
     * Initializing Facebook.
     */
    private void initFacebookClient() {
        FacebookSdk.sdkInitialize(activity);
        fbCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location, picture");

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        String token = loginResult.getAccessToken().getToken();
                        AuthResult result = new AuthResult(object.toString(), SocialType.FACEBOOK);
                        result.getAuthUser().setAccessToken(token);
                        callback.onSuccess(result);
                    }
                });

                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }

            @Override
            public void onError(FacebookException exception) {
                callback.onError(exception);
            }
        });
    }

    public void setCallback(SocialAuthCallback callback) {
        this.callback = callback;
    }

    /**
     * Login to Facebook or google.
     *
     * @param type
     */
    public void login(SocialType type) {
        this.socialType = type;
        switch (socialType) {
            case GOOGLE:
                shouldResolve = true;
                googleApiClient.connect();
                break;
            case FACEBOOK:
                LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends", "email"));
                break;
        }
    }

    /**
     * Disconnect from facebook or google.
     */
    public void disconnect() {
        if (socialType == SocialType.GOOGLE) {
            googleApiClient.disconnect();
            Timber.d("google logout");
        } else if (socialType == SocialType.FACEBOOK) {
            LoginManager.getInstance().logOut();
            Timber.d("facebook logout");
        }
    }

    /**
     * Retrieving access token from google.
     */
    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        String jsonData;
        String email;

        public RetrieveTokenTask(String jsonData, String email) {
            this.jsonData = jsonData;
            this.email = email;
        }

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(activity, accountName, scopes);
            } catch (IOException e) {
                callback.onError(e);
            } catch (UserRecoverableAuthException e) {
                activity.startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                callback.onError(e);
            }

            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            if (token != null) {
                AuthResult result = new AuthResult(jsonData, SocialType.GOOGLE);
                result.getAuthUser().setAccessToken(token);
                result.getAuthUser().setEmail(email);
                callback.onSuccess(result);
            }
        }
    }

}
