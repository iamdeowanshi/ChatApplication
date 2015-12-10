package com.mtvindia.connect.data.repository;

import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;

import java.util.List;

/**
 * Created by Sibi on 02/12/15.
 */
public interface ChatListRepository extends BaseRepository<ChatList> {

    ChatMessage lastMessage(int userId);

    List<ChatList> sortList();

    long size();

    void updateTime(long id, String time);

    boolean searchUser(long userId);

}


