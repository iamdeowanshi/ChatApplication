package com.mtvindia.connect.app.di;

import com.mtvindia.connect.presenter.SocialAuthPresenter;
import com.mtvindia.connect.presenter.concrete.SocialAuthPresenterImpl;

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
    public SocialAuthPresenter provideSamplePresenter() {
        return new SocialAuthPresenterImpl();
    }

}
