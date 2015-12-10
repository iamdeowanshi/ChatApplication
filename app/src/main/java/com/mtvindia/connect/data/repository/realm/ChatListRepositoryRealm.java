package com.mtvindia.connect.data.repository.realm;

import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatListRepository;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by Sibi on 02/12/15.
 */
public class ChatListRepositoryRealm extends BaseRepositoryRealm<ChatList> implements ChatListRepository {

    public ChatListRepositoryRealm() {
        super(ChatList.class);
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

}
