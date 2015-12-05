package com.mtvindia.connect.data.repository.realm;


import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.repository.BaseRepository;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmObject;

public abstract class BaseRepositoryRealm<T extends RealmObject> implements BaseRepository<T> {

    @Inject Realm realm;

    protected Class<T> modelType;

    public BaseRepositoryRealm(Class<T> modelType) {
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

}
