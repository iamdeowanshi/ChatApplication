package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aaditya Deowanshi
 */

public class User extends BaseModel {

    @SerializedName("id")
    private int id;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("profilePic")
    private String profilePic;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("birthDate")
    private String birthDate;
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("about")
    private String about;
    @SerializedName("likeToMeet")
    private String likeToMeet;
    @SerializedName("interestedIn")
    private String interestedIn;
    @SerializedName("gender")
    private String gender;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAuthHeader() {
        return "Bearer " + accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getLikeToMeet() {
        return likeToMeet;
    }

    public void setLikeToMeet(String likeToMeet) {
        this.likeToMeet = likeToMeet;
    }

    public String getInterestedIn() {
        return interestedIn;
    }

    public void setInterestedIn(String interestedIn) {
        this.interestedIn = interestedIn;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullName() {
        String name = (lastName != null) ? firstName + " " + lastName : firstName;

        return name;
    }

}
