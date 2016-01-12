package com.mtvindia.connect.presenter;

/**
 * Created by Sibi on 24/12/15.
 */
public interface ChatListPresenter {

    void addUser(int id, int chatUserId, String header);

    void removeUser(int chatUserId, String header);

    void getChatUsers(String header);


}
