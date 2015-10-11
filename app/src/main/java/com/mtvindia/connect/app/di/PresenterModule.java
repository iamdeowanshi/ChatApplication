package com.mtvindia.connect.app.di;

import com.mtvindia.connect.presenter.SamplePresenter;
import com.mtvindia.connect.presenter.concrete.SamplePresenterImpl;

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
    public SamplePresenter provideSamplePresenter() {
        return new SamplePresenterImpl();
    }

}
