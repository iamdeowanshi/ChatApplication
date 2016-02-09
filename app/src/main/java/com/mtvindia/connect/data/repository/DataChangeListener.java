package com.mtvindia.connect.data.repository;

import java.util.List;

/**
 * Created by Sibi on 10/12/15.
 */
public interface DataChangeListener<T> {

    void onRealmDataChange(List<T> updatedData);

    void onStatusChange(String status);
}
