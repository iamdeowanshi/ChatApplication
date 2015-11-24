package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sibi on 20/11/15.
 */
public class AboutUser {

    @SerializedName("id")
    private int id;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("profilePic")
    private String profilePic;
    @SerializedName("about")
    private String about;
    @SerializedName("commonThingsCount")
    private int commonThingsCount;

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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getCommonThingsCount() {
        return commonThingsCount;
    }

    public void setCommonThingsCount(int commonThingsCount) {
        this.commonThingsCount = commonThingsCount;
    }

    public String getFullName() {
        String name = (lastName != null) ? firstName + " " + lastName : firstName;

        return name;
    }

}
