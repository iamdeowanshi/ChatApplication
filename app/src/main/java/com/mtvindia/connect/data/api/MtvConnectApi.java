package com.mtvindia.connect.data.api;

import com.mtvindia.connect.app.Config;
import com.mtvindia.connect.data.model.User;

import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * @author Farhan Ali
 */
public interface MtvConnectApi {

    @POST(Config.USER_LOGIN)
    Observable<User> login(
            @Query("q") String query, @Query("page") int page);


}