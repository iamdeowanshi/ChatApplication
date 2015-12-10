package com.mtvindia.connect.data.repository.realm;

import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatMessageRepository;

import java.util.List;

/**
 * Created by Sibi on 02/12/15.
 */
public class ChatMessageRepositoryRealm extends BaseRepositoryRealm<ChatMessage> implements ChatMessageRepository {

    public ChatMessageRepositoryRealm() {
        super(ChatMessage.class);
    }

    @Override
    public void save(ChatMessage obj) {
        realm.beginTransaction();
        if( realm.where(modelType).count() != 0) {
            obj.setId((int) (getNextKey() + 1));
        }
        realm.copyToRealmOrUpdate(obj);
        realm.commitTransaction();
    }

    private long getNextKey() {
        return realm.where(ChatMessage.class).maximumInt("id");
    }

    @Override
    public List<ChatMessage> searchMessage(int userId) {
        return realm.where(modelType)
                .equalTo("userId", userId)
                .findAll();
    }

}
