package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.User;

/**
 * @author Farhan Ali
 */
public interface LoginViewInteractor extends ViewInteractor {

    void showProgress();

    void hideProgress();

    void loginDone(User user, boolean isRegister);

    void onError(Throwable tr);

}
