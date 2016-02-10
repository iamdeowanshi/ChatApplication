package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aaditya Deowanshi
 */

public class PushMessage {

    @SerializedName("fromUserId")
    private int id;
    private String name;
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
