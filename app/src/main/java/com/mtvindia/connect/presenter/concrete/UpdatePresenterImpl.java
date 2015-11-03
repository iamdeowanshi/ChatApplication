package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.UpdatePresenter;
import com.mtvindia.connect.presenter.UpdateViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Sibi on 28/10/15.
 */
public class UpdatePresenterImpl extends BaseNetworkPresenter<UpdateViewInteractor> implements UpdatePresenter {

    @Inject MtvConnectApi mtvConnectApi;

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

    @Override
    public void update(User user) {
        viewInteractor.showProgress();

        Observable<User> resultObservable = mtvConnectApi.update(user, user.getAuthHeader());

        subscribeForNetwork(resultObservable, apiObserver);
    }

}
