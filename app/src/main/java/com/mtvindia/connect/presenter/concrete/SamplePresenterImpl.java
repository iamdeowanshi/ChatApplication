package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BasePresenter;
import com.mtvindia.connect.presenter.SamplePresenter;
import com.mtvindia.connect.presenter.SampleViewInteractor;

/**
 * @author Farhan Ali
 */
public class SamplePresenterImpl extends BasePresenter<SampleViewInteractor>
        implements SamplePresenter {

    @Override
    public void doSomething() {
        viewInteractor.showSomeMessage("Doing something..");
        viewInteractor.showSomeMessage("Done something");
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

}
