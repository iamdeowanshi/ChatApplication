package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sibi on 02/11/15.
 */
public class MatchUserResponse {
    @SerializedName("userId")
    private int id;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("profilePic")
    private String profiePic;
}
