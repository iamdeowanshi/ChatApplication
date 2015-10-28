package com.mtvindia.connect.data.model;

import com.mtvindia.connect.util.social.AuthResult;

/**
 * Created by Sibi on 28/10/15.
 */
public class GoogleLoginRequest extends LoginRequest {

    private String gplusId;

    public GoogleLoginRequest() {
    }

    public GoogleLoginRequest(AuthResult authResult) {
        super(authResult);
        gplusId = authResult.getAuthUser().getSocialId();
    }

    public String getGplusId() {
        return gplusId;
    }

    public void setGplusId(String gplusId) {
        this.gplusId = gplusId;
    }

}
