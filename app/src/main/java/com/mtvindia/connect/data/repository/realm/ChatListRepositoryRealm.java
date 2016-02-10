package com.mtvindia.connect.data.repository.realm;

import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * @author Aaditya Deowanshi
 *
 *         Chat list repository class to save list of active chat users.
 */

public class ChatListRepositoryRealm extends BaseRepositoryRealm<ChatList> implements ChatListRepository {


    private DataChangeListener dataChangeListener;
    private RealmChangeListener realmListener;
    private ChatMessage chatMessage;
    private String status = "Offline";

    public ChatListRepositoryRealm() {
        super(ChatList.class);
        realmListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if (dataChangeListener != null) {
                    dataChangeListener.onStatusChange(status);
                    dataChangeListener.onRealmDataChange(null);
                }
            }
        };
    }

    /**
     * Saves chat list object to database.
     * @param chatList
     */
    @Override
    public void save(ChatList chatList) {
        realm.beginTransaction();

        if (realm.where(modelType).count() != 0) {
            chatList.setId((int) (getNextKey() + 1));
        }

        realm.copyToRealmOrUpdate(chatList);
        realm.commitTransaction();
        realm.addChangeListener(realmListener);
    }

    /**
     * Returns the last message for a particular user in chat list.
     * @param chatUserId
     * @param userId
     * @return
     */
    @Override
    public ChatMessage lastMessage(int chatUserId, int userId) {
        List<ChatMessage> chatMessages = realm.where(ChatMessage.class)
                .equalTo("from", "webuser" + chatUserId)
                .equalTo("to", "webuser" + userId)
                .or()
                .equalTo("from", "webuser" + userId)
                .equalTo("to", "webuser" + chatUserId)
                .findAll();

        if (chatMessages.size() == 0) {
            chatMessage = new ChatMessage();
            chatMessage.setBody("");
            chatMessage.setCreatedTime("");

            return chatMessage;
        }

        return chatMessage = chatMessages.get(chatMessages.size() - 1);
    }

    /**
     * Sorting chat list on the basis of last interacted time.
     * @param userId
     * @return
     */
    @Override
    public List<ChatList> sortList(int userId) {
        RealmResults<ChatList> chatLists = realm.where(modelType)
                .equalTo("logedinUser", userId)
                .findAll();

        chatLists.sort("time", RealmResults.SORT_ORDER_DESCENDING);

        return chatLists;
    }

    /**
     * Returns size of chat list.
     * @return
     */
    @Override
    public long size() {
        return realm.where(modelType).count();
    }

    /**
     * Updates the last interacted time for a cha
     * @param id
     * @param userId
     * @param time
     */
    @Override
    public void updateTime(long userId, int id, String time) {
        realm.beginTransaction();
        ChatList item = find(userId, id);
        item.setTime(time);
        realm.commitTransaction();
    }

    /**
     * Searching for a users in database.
     * @param id
     * @return
     */
    @Override
    public ChatList searchUser(long id) {
        return realm.where(modelType).equalTo("logedinUser", id).findFirst();
    }

    /**
     * Returns  chat list object of a user.
     * @param id
     * @param userId
     * @return
     */
    @Override
    public ChatList getUser(int id, int userId) {
        return realm.where(modelType)
                            .equalTo("userId", id)
                            .equalTo("logedinUser", userId)
                            .findFirst();
    }

    @Override
    public void remove(long id, int userId) {
        realm.beginTransaction();
        find(id, userId).removeFromRealm();
        realm.commitTransaction();
        realm.addChangeListener(realmListener);
    }

    /**
     * Checking whether user is presetn in database or not
     * @param userId
     * @param logedinUser
     * @return
     */
    @Override
    public boolean searchUser(long userId, long logedinUser) {
        return (realm.where(modelType)
                .equalTo("userId", userId)
                .equalTo("logedinUser", logedinUser).count() != 0);
    }

    /**
     * Returns the status of user : offline or online.
     * @param id
     * @param userId
     * @return
     */
    @Override
    public String getStatus(int id, int userId) {
        ChatList chatList = realm.where(modelType).equalTo("userId", id).equalTo("logedinUser", userId).findFirst();

        if (chatList == null) return "offline";

        return chatList.getStatus();
    }

    /**
     * Updates status of user.
     * @param id
     * @param userId
     * @param status
     */
    @Override
    public void updateStatus(int id, int userId, String status) {
      realm.beginTransaction();
        ChatList item = find(id, userId);

        if (item != null) {
            item.setStatus(status);
            realm.copyToRealmOrUpdate(item);
            this.status = status;
        }

        realm.commitTransaction();
        realm.addChangeListener(realmListener);
    }

    @Override
    public void setDataChangeListener(DataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    @Override
    public void removeDataChangeListener() {
        super.removeDataChangeListener();
        realm.removeChangeListener(realmListener);
    }

}
