package com.mtvindia.connect.data.repository.realm;

import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Sibi on 02/12/15.
 */
public class ChatListRepositoryRealm extends BaseRepositoryRealm<ChatList> implements ChatListRepository {

    private DataChangeListener dataChangeListener;
    private RealmChangeListener realmListener;
    private String status = "Offline";

    public ChatListRepositoryRealm() {
        super(ChatList.class);
        realmListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if( dataChangeListener != null) {
                    dataChangeListener.onStatusChanged(status);
                    dataChangeListener.onChange(null);
                }
            }
        };
    }

    @Override
    public void save(ChatList obj) {
        realm.beginTransaction();
        if (realm.where(modelType).count() != 0) {
            obj.setId((int) (getNextKey() + 1));
        }
        realm.copyToRealmOrUpdate(obj);
        realm.commitTransaction();
        realm.addChangeListener(realmListener);
    }

    private long getNextKey() {
        return realm.where(ChatList.class).maximumInt("id");
    }

    @Override
    public ChatMessage lastMessage(int id, int userId) {
        List<ChatMessage> result = realm.where(ChatMessage.class)
                .equalTo("from", "webuser" + id)
                .equalTo("to", "webuser" + userId)
                .or()
                .equalTo("from", "webuser" + userId)
                .equalTo("to", "webuser" + id)
                .findAll();

        return result.get(result.size() -1);
    }

    @Override
    public List<ChatList> sortList(int id) {
        RealmResults<ChatList> result = realm.where(modelType)
                                            .equalTo("logedinUser", id)
                                          .findAll();
        result.sort("time", RealmResults.SORT_ORDER_DESCENDING);

        return result;
    }

    @Override
    public long size() {
        return realm.where(modelType)
                    .count();
    }

    @Override
    public void updateTime(long id, int userId, String time) {
        realm.beginTransaction();
        ChatList item = find(id, userId);
        item.setTime(time);
        realm.commitTransaction();
    }

    @Override
    public ChatList searchChat(long id) {
        return realm.where(modelType).equalTo("logedinUser", id).findFirst();
    }

    @Override
    public void remove(long id, int userId) {
        realm.beginTransaction();
        find(id, userId).removeFromRealm();
        realm.commitTransaction();
        realm.addChangeListener(realmListener);
    }

    @Override
    public boolean searchUser(long userId, long logedinUser) {
        return (realm.where(modelType)
                .equalTo("userId", userId)
                .equalTo("logedinUser", logedinUser).count() != 0);
    }

    @Override
    public String getStatus(int id, int userId) {
        ChatList chatList = realm.where(modelType).equalTo("userId", id).equalTo("logedinUser", userId).findFirst();

        return chatList.getStatus();
    }

    @Override
    public void updateStatus(int id, int userId, String status) {
        realm.beginTransaction();
        ChatList item = find(id, userId);
        if(item != null) {
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
