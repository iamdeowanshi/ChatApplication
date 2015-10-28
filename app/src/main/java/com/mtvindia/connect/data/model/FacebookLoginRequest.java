package com.mtvindia.connect.data.model;

import com.mtvindia.connect.util.social.AuthResult;

/**
 * Created by Sibi on 28/10/15.
 */
public class FacebookLoginRequest extends LoginRequest {

    private String facebookId;

    public FacebookLoginRequest() {
    }

    public FacebookLoginRequest(AuthResult authResult) {
        super(authResult);
        facebookId = authResult.getAuthUser().getSocialId();
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

}
