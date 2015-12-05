package com.mtvindia.connect.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sibi on 26/11/15.
 */

public class ChatList extends RealmObject {


    @PrimaryKey
    private int id;
    private int chatId;
    private String image;
    private String name;
    private String lastMessage;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int userId) {
        this.id = userId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
