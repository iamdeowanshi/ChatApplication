package com.mtvindia.connect.app.base;

import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.util.RxUtil;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * An extension of BasePresenter with facility to subscribe and unsubscribe rx.subscriptions.
 * Presenters that are going to interact with api/network can extend this.
 *
 * @author Farhan Ali
 */
public abstract class BaseNetworkPresenter<T extends ViewInteractor> extends BasePresenter<T> {

    protected CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    public void resume() {
        subscriptions = RxUtil.initSubscription(subscriptions);
    }

    @Override
    public void pause() {
        RxUtil.unsubscribe(subscriptions);
    }

    protected void subscribeForNetwork(Observable resultObservable, ApiObserver apiObserver) {
        RxUtil.subscribeForNetwork(subscriptions, resultObservable, apiObserver);
    }

}
