package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.LoginResponse;

/**
 * @author Farhan Ali
 */
public interface LoginViewInteractor extends ViewInteractor {

    void showProgress();

    void hideProgress();

    void loginDone(LoginResponse loginResponse);

    void onError(Throwable tr);

}
