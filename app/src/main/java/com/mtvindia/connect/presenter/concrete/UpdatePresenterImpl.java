package com.mtvindia.connect.presenter.concrete;

import com.google.gson.Gson;
import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.UpdatePresenter;
import com.mtvindia.connect.presenter.UpdateViewInteractor;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

/**
 * @author Aaditya Deowanshi
 *
 *         UpdatePresenter to update details about user.
 */

public class UpdatePresenterImpl extends BaseNetworkPresenter<UpdateViewInteractor> implements UpdatePresenter {

    @Inject MtvConnectApi mtvConnectApi;
    @Inject Gson gson;

    public UpdatePresenterImpl() {
        injectDependencies();
    }

    private ApiObserver<User> apiObserver = new ApiObserver<User>() {
        @Override
        public void onResult(User result) {
            viewInteractor.hideProgress();
            viewInteractor.updateDone(result);
        }

        @Override
        public void onError(Throwable e) {
            viewInteractor.onError(e);
        }
    };

    private ApiObserver<User> apiObserverLocation = new ApiObserver<User>() {
        @Override
        public void onResult(User result) {
            viewInteractor.updateDone(result);
        }

        @Override
        public void onError(Throwable e) {
        }
    };

    @Override
    public void update(User user) {
        viewInteractor.showProgress();
        Timber.e(gson.toJson(user));

        Observable<User> resultObservable = mtvConnectApi.update(user, user.getAuthHeader());

        subscribeForNetwork(resultObservable, apiObserver);
    }

    @Override
    public void updateLocation(User user) {
        Observable<User> resultObservable = mtvConnectApi.update(user, user.getAuthHeader());

        subscribeForNetwork(resultObservable, apiObserverLocation);
    }

}
