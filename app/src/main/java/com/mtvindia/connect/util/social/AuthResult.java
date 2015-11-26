package com.mtvindia.connect.util.social;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sibi on 26/10/15.
 */
public class AuthResult {

    private AuthUser authUser;
    private String data;
    private SocialAuth.SocialType authType;

    public AuthResult(String data, SocialAuth.SocialType authType) {
        this.data = data;
        this.authType = authType;
        loadUserData();
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    public String getData() {
        return data;
    }

    public SocialAuth.SocialType getAuthType() {
        return authType;
    }

    private void loadUserData() {
        switch (authType) {
            case GOOGLE:
                loadGoogleUserData();
                break;
            case FACEBOOK:
                loadFbUserData();
                break;
        }
    }

    private void loadGoogleUserData() {
        try {
            JSONObject jsonObject = new JSONObject(data);
            authUser = new AuthUser();
            authUser.setSocialId(jsonObject.getString("id"));
            loadGoogleName(authUser, jsonObject.getJSONObject("name"));
            authUser.setProfilePic(getGoogleProfileUrl(jsonObject.getJSONObject("image")) + "0");
            authUser.setGender(jsonObject.getString("gender"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getGoogleProfileUrl(JSONObject jsonObject) {
        try {
            return jsonObject.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void loadGoogleName(AuthUser authUser, JSONObject jsonObject) {
        try {
            authUser.setFirstName(jsonObject.getString("givenName"));
            authUser.setLastName(jsonObject.getString("familyName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadFbUserData() {
        try {
            JSONObject jsonObject = new JSONObject(data);
            authUser = new AuthUser();
            authUser.setSocialId(jsonObject.getString("id"));
            authUser.setFirstName(jsonObject.getString("first_name"));
            authUser.setLastName(jsonObject.getString("last_name"));
            authUser.setProfilePic("http://graph.facebook.com/" + jsonObject.getString("id") + "/picture?type=large");
            authUser.setGender(jsonObject.getString("gender"));
            authUser.setEmail(jsonObject.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getFbProfileUrl(JSONObject jsonObject) {
        try {
            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
            return dataJsonObject.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

}
