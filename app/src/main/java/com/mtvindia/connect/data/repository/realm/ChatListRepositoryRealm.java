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
    private String status;
    public ChatListRepositoryRealm() {
        super(ChatList.class);
        realmListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if( dataChangeListener != null) {
                    dataChangeListener.onStatusChanged(status);
                }
            }
        };
    }

    @Override
    public ChatMessage lastMessage(int userId) {
        List<ChatMessage> result = realm.where(ChatMessage.class)
                .equalTo("userId", userId)
                .findAll();

        return result.get(result.size() -1);
    }

    @Override
    public List<ChatList> sortList() {
        RealmResults<ChatList> result = realm.where(modelType)
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
    public void updateTime(long id, String time) {
        realm.beginTransaction();
        ChatList item = find(id);
        item.setTime(time);
        realm.commitTransaction();
    }

    @Override
    public boolean searchUser(long userId) {
        return (realm.where(modelType).equalTo("id", userId).count() != 0);
    }

    @Override
    public void updateStatus(int id, String status) {
        realm.beginTransaction();
        ChatList item = find(id);
        if(item != null) item.setStatus(status);
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
        this.status = status;
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
