package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BasePresenter;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.SocialAuthPresenter;
import com.mtvindia.connect.presenter.SocialAuthViewInteractor;

/**
 * @author Farhan Ali
 */
public class SocialAuthPresenterImpl extends BasePresenter<SocialAuthViewInteractor>
        implements SocialAuthPresenter {

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
