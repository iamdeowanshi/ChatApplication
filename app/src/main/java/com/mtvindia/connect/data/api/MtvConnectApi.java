package com.mtvindia.connect.data.api;

import com.mtvindia.connect.app.Config;
import com.mtvindia.connect.data.model.LoginRequest;
import com.mtvindia.connect.data.model.LoginResponse;
import com.mtvindia.connect.data.model.User;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * @author Farhan Ali
 */
public interface MtvConnectApi {

    @POST(Config.USER_LOGIN)
    Observable<LoginResponse> login(@Body LoginRequest request);

    @POST(Config.USER_UPDATE)
    Observable<User> update(@Body User user);


}