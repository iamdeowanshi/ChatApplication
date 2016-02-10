package com.mtvindia.connect.presenter;

/**
 * @author Aaditya Deowanshi
 */

public interface ChatListPresenter {

    void addUser(int id, int chatUserId, String header);

    void removeUser(int chatUserId, String header);

    void getChatUsers(String header);

}
