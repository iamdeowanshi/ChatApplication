package com.mtvindia.connect.data.repository.realm;

import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;

import java.util.List;

import io.realm.RealmChangeListener;

/**
 * Created by Sibi on 02/12/15.
 */
public class ChatMessageRepositoryRealm extends BaseRepositoryRealm<ChatMessage> implements ChatMessageRepository {

    private RealmChangeListener realmListener;
    private DataChangeListener dataChangeListener;

    public ChatMessageRepositoryRealm() {
        super(ChatMessage.class);
        realmListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if( dataChangeListener != null) {
                    dataChangeListener.onChange(null);
                }
            }
        };
    }

    @Override
    public void save(ChatMessage obj) {
        realm.beginTransaction();
        if (realm.where(modelType).count() != 0) {
            obj.setId((int) (getNextKey() + 1));
        }
        realm.copyToRealmOrUpdate(obj);
        realm.commitTransaction();
        realm.addChangeListener(realmListener);
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
