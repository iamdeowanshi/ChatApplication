package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;
import com.mtvindia.connect.util.social.AuthResult;

/**
 * Created by Sibi on 28/10/15.
 */
public abstract class LoginRequest {

    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("profilePic")
    private String profilePic;
    @SerializedName("accessToken")
    private String accessToken;

    public LoginRequest() {
    }

    public LoginRequest(AuthResult authResult) {
        firstName = authResult.getAuthUser().getFirstName();
        lastName = authResult.getAuthUser().getLastName();
        email = authResult.getAuthUser().getEmail();
        profilePic = authResult.getAuthUser().getProfilePic();
        accessToken = authResult.getAuthUser().getAccessToken();
    }

    public LoginRequest(String firstName, String lastName, String email, String profilePic, String accessToken) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePic = profilePic;
        this.accessToken = accessToken;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
