package com.mtvindia.connect.data.repository;

import java.util.List;

/**
 * @author Aaditya Deowanshi
 */

public interface DataChangeListener<T> {

    void onRealmDataChange(List<T> updatedData);

    void onStatusChange(String status);

}
