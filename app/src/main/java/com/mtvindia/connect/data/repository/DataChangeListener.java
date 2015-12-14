package com.mtvindia.connect.data.repository;

import java.util.List;

/**
 * Created by Sibi on 10/12/15.
 */
public interface DataChangeListener<T> {

    void onChange(List<T> updatedData);

    void onStatusChanged(String status);
}
