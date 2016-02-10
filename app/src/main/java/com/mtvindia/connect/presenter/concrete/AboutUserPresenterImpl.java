package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.AboutUser;
import com.mtvindia.connect.presenter.AboutUserPresenter;
import com.mtvindia.connect.presenter.AboutUserViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Aaditya Deowanshi
 *
 *         About user presenter class to fetch details about user.
 */

public class AboutUserPresenterImpl extends BaseNetworkPresenter<AboutUserViewInteractor> implements AboutUserPresenter {

    @Inject MtvConnectApi mtvConnectApi;

    public AboutUserPresenterImpl() {
        injectDependencies();
    }

    private ApiObserver<AboutUser> apiObserver = new ApiObserver<AboutUser>() {
        @Override
        public void onResult(AboutUser result) {
            viewInteractor.hideProgress();
            viewInteractor.aboutUser(result);

        }

        @Override
        public void onError(Throwable e) {
            viewInteractor.onError(e);
        }
    };

    @Override
    public void getAboutUser(int id, String header) {
        viewInteractor.showProgress();
        Observable<AboutUser> resultObservable = mtvConnectApi.aboutUser(id, header);

        subscribeForNetwork(resultObservable, apiObserver);
    }

}
