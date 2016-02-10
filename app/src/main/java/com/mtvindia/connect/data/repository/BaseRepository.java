package com.mtvindia.connect.data.repository;

import java.util.List;

/**
 * @author Aaditya Deowanshi
 */

public interface BaseRepository<T> {

    void save(T obj);

    T find(long id, int userId);

    List<T> readAll();

    List<T> findAll(long id, int userId);

    void remove(long id, int userId);

    void setDataChangeListener(DataChangeListener changeListener);

    void removeDataChangeListener();

    void reInitialize();

    long getNextKey();

}
