package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;
import com.mtvindia.connect.data.model.LoginRequest;

/**
 * @author Farhan Ali
 */
public interface LoginPresenter extends Presenter<LoginViewInteractor> {

    void login(LoginRequest request);

}
