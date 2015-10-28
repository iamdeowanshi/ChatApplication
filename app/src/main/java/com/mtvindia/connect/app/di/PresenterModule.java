package com.mtvindia.connect.app.di;

import com.mtvindia.connect.presenter.LoginPresenter;
import com.mtvindia.connect.presenter.concrete.LoginPresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Provides all presenter class dependencies.
 *
 * @author Farhan Ali
 */
@Module(
        complete = false,
        library = true
)
public class PresenterModule {

    @Provides
    public LoginPresenter provideSamplePresenter() {
        return new LoginPresenterImpl();
    }

}
