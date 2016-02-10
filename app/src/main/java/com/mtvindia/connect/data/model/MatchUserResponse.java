package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aaditya Deowanshi
 */

public class MatchUserResponse {

    @SerializedName("Id")
    private int id;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("profilePic")
    private String profilePic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFullName() {
        String name = (lastName != null) ? firstName + " " + lastName : firstName;

        return name;
    }

}
