package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sibi on 26/10/15.
 */
public class User extends BaseModel {

    @SerializedName("id")
    private int id;
    @SerializedName("signup")
    private int signup;
    @SerializedName("socialId")
    private String socialId;
    @SerializedName("profilePic")
    private String profilePic;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("gender")
    private String gender;
    @SerializedName("accessToken")
    private String accessToken;

    public User() {
    }

    public User(LoginResponse response) {
        id = response.getId();
        firstName = response.getFirstName();
        lastName = response.getLastName();
        email = response.getEmail();
        profilePic = response.getProfilePic();
        accessToken = response.getAccessToken();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSignup() {
        return signup;
    }

    public void setSignup(int signup) {
        this.signup = signup;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
