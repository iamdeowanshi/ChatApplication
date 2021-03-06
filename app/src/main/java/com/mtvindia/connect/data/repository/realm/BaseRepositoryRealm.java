package com.mtvindia.connect.data.repository.realm;


import android.content.Context;

import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.app.di.OrmModule;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.BaseRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * @author Aaditya Deowanshi
 *
 *         BaseRepositoryRealm class to perform basic CRUD operation on realm database.
 */

public abstract class BaseRepositoryRealm<T extends RealmObject> implements BaseRepository<T> {

    @Inject Realm realm;
    @Inject Context context;

    protected Class<T> modelType;

    public BaseRepositoryRealm(final Class<T> modelType) {
        this.modelType = modelType;
        Injector.instance().inject(this);
    }

    @Override
    public void save(T obj) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(obj);
        realm.commitTransaction();
    }

    @Override
    public T find(long userId, int id) {
        return realm.where(modelType).equalTo("userId", userId).equalTo("logedinUser", id).findFirst();
    }

    /**
     * Returns the last key value in database.
     *
     * @return
     */
    @Override
    public long getNextKey() {
        return realm.where(ChatMessage.class).maximumInt("id");
    }

    @Override
    public List<T> findAll(long id, int userId) {
        return realm.where(modelType).equalTo("userId", id).equalTo("logedinUser", userId).findAll();
    }

    @Override
    public List<T> readAll() {
        return realm.where(modelType).findAll();
    }

    @Override
    public void remove(long id, int userId) {
        realm.beginTransaction();
        find(id, userId).removeFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void reInitialize() {
        realm = new OrmModule().provideRealm(context);
    }

}
