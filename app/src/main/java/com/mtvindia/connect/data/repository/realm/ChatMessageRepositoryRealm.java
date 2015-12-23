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
                if (dataChangeListener != null) {
                    dataChangeListener.onChange(null);
                }
            }
        };
    }

    @Override
    public void removeAllMessage(long from, int to) {
        realm.beginTransaction();
        List<ChatMessage> result = searchMessage("webuser" + from, "webuser" + to);
        result.clear();
        realm.commitTransaction();
        realm.addChangeListener(realmListener);
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
    public List<ChatMessage> searchMessage(String from, String to) {
        return realm.where(modelType)
                .equalTo("from", from)
                .equalTo("to", to)
                .or()
                .equalTo("from", to)
                .equalTo("to", from)
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
