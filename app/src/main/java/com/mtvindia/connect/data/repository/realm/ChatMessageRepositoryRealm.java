package com.mtvindia.connect.data.repository.realm;

import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;
import com.mtvindia.connect.util.UserPreference;

import java.util.List;

import javax.inject.Inject;

import io.realm.RealmChangeListener;

/**
 * @author Aaditya Deowanshi
 *
 *         ChatMessageRepository to save all sent and received messages.
 */

public class ChatMessageRepositoryRealm extends BaseRepositoryRealm<ChatMessage> implements ChatMessageRepository {

    @Inject UserPreference userPreference;

    private RealmChangeListener realmListener;
    private DataChangeListener dataChangeListener;

    public ChatMessageRepositoryRealm() {
        super(ChatMessage.class);
        realmListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if (dataChangeListener != null) {
                    dataChangeListener.onRealmDataChange(null);
                }
            }
        };
    }

    /**
     * Removes all messages from database for particular user.
     *
     * @param from
     * @param to
     */
    @Override
    public void removeAllMessage(long from, int to) {
        realm.beginTransaction();
        List<ChatMessage> result = searchMessage("webuser" + from, "webuser" + to);
        result.clear();
        realm.commitTransaction();
        realm.addChangeListener(realmListener);
    }

    /**
     * Returns all unsent messages.
     *
     * @return
     */
    @Override
    public List<ChatMessage> unsentMessages() {
        return realm.where(modelType)
                .equalTo("status", "Sending")
                .equalTo("userId", userPreference.readUser().getId())
                .findAll();
    }

    /**
     * Saves messages to the database.
     *
     * @param obj
     */
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

    /**
     * Returns all messages for a particular user.
     *
     * @param from
     * @param to
     * @return
     */
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
        realm.removeChangeListener(realmListener);
    }

}
