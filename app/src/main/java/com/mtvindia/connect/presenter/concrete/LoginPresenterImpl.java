package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BasePresenter;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.LoginPresenter;
import com.mtvindia.connect.presenter.LoginViewInteractor;

/**
 * @author Farhan Ali
 */
public class LoginPresenterImpl extends BasePresenter<LoginViewInteractor>
        implements LoginPresenter {

    @Override
    public void login(User user) {
       // viewInteractor.showSomeMessage("Doing something..");
       // viewInteractor.showSomeMessage("Done something");
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

}
