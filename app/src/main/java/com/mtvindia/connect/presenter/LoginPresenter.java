package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;
import com.mtvindia.connect.data.model.User;

/**
 * @author Farhan Ali
 */
public interface LoginPresenter extends Presenter<LoginViewInteractor> {

    void login(User user);
}
