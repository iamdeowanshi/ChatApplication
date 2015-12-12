package com.mtvindia.connect.data.repository.realm;


import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.repository.BaseRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;

public abstract class BaseRepositoryRealm<T extends RealmObject> implements BaseRepository<T> {

    @Inject Realm realm;

    protected Class<T> modelType;
    private RealmChangeListener realmListener;
    private DataChangeListener dataChangeListener;

    public BaseRepositoryRealm(final Class<T> modelType) {
        this.modelType = modelType;
        Injector.instance().inject(this);
        //setDataChangeListener();
    }

    @Override
    public void save(T obj) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(obj);
        realm.commitTransaction();
    }

    @Override
    public T find(long id) {
        return realm.where(modelType).equalTo("id", id).findFirst();
    }

    @Override
    public List<T> readAll() {
        return realm.where(modelType).findAll();
    }

    @Override
    public void remove(long id) {
        realm.beginTransaction();
        find(id).removeFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void setDataChangeListener(DataChangeListener changeListener) {
        //this.dataChangeListener = changeListener;
    }

    @Override
    public void removeDataChangeListener() {
        //realm.removeChangeListener(realmListener);
    }
}
