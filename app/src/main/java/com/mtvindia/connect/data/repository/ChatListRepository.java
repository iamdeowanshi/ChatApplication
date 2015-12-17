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

    ChatList searchChat(long id);

    boolean searchUser(long userId, long id);

    void updateStatus(int id, int userId, String status);

}


