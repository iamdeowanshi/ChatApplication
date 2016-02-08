package com.mtvindia.connect.data.repository;

import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;

import java.util.List;

/**
 * Created by Sibi on 02/12/15.
 */
public interface ChatListRepository extends BaseRepository<ChatList> {

    ChatMessage lastMessage(int id, int userId);

    List<ChatList> sortList(int id);

    long size();

    void updateTime(long id,int userId, String time);

    ChatList searchUser(long id);

    ChatList getUser(int id, int userId);

    boolean searchUser(long userId, long id);

    String getStatus(int id, int userId);
    void updateStatus(int id, int userId, String status);

}


