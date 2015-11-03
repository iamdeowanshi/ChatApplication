package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;
import com.mtvindia.connect.util.social.AuthResult;

/**
 * Created by Sibi on 28/10/15.
 */
public class LoginRequest {

    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("profilePic")
    private String profilePic;
    @SerializedName("socialType")
    private String socialType;
    @SerializedName("socialId")
    private String socialId;
    @SerializedName("accessToken")
    private String accessToken;

    public LoginRequest() {
    }

    public LoginRequest(AuthResult authResult) {
        firstName = authResult.getAuthUser().getFirstName();
        lastName = authResult.getAuthUser().getLastName();
        email = authResult.getAuthUser().getEmail();
        profilePic = authResult.getAuthUser().getProfilePic();
        socialId = authResult.getAuthUser().getSocialId();
        socialType = authResult.getAuthType().toString();
        accessToken = authResult.getAuthUser().getAccessToken();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
