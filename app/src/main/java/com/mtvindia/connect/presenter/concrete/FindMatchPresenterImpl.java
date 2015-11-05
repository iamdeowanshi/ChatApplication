package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.FindMatchPresenter;
import com.mtvindia.connect.presenter.FindMatchViewInteractor;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Sibi on 05/11/15.
 */
public class FindMatchPresenterImpl extends BaseNetworkPresenter<FindMatchViewInteractor> implements FindMatchPresenter {

    @Inject
    MtvConnectApi mtvConnectApi;

    public FindMatchPresenterImpl() {
        injectDependencies();
    }

    private ApiObserver<List<User>> apiObserver = new ApiObserver<List<User>>() {
        @Override
        public void onResult(List<User> result) {
            viewInteractor.hideProgress();
            viewInteractor.showUsers(result);
        }

        @Override
        public void onError(Throwable e) {
            viewInteractor.hideProgress();
            viewInteractor.onError(e);
        }
    };

    @Override
    public void findMatches(String header) {
        viewInteractor.showProgress();

        Observable<List<User>> observable = mtvConnectApi.matchUser(header);

        subscribeForNetwork(observable, apiObserver);

    }

}
