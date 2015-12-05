package com.mtvindia.connect.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sibi on 01/12/15.
 */
public class ChatMessage extends RealmObject {

    @PrimaryKey
    private int id;
    private int userId;
    private String body;
    private String createdTime;
    private String status;
    private String from;
    private String to;

    public ChatMessage() {
    }

    public ChatMessage(int id, int userId, String body, String createdTime, String status, String from, String to) {
        this.id = id;
        this.userId = userId;
        this.body = body;
        this.createdTime = createdTime;
        this.status = status;
        this.from = from;
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
