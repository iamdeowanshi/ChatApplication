package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.LoginRequest;
import com.mtvindia.connect.data.model.LoginResponse;
import com.mtvindia.connect.presenter.LoginPresenter;
import com.mtvindia.connect.presenter.LoginViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Farhan Ali
 */
public class LoginPresenterImpl extends BaseNetworkPresenter<LoginViewInteractor>
        implements LoginPresenter {

    @Inject MtvConnectApi mtvConnectApi;

    public LoginPresenterImpl() {
        injectDependencies();
    }

    private ApiObserver<LoginResponse> apiObserver = new ApiObserver<LoginResponse>() {
        @Override
        public void onResult(LoginResponse result) {
            viewInteractor.hideProgress();
            viewInteractor.loginDone(result);
        }

        @Override
        public void onError(Throwable e) {
            viewInteractor.hideProgress();
            viewInteractor.onError(e);
        }
    };

    @Override
    public void login(LoginRequest request) {
        viewInteractor.showProgress();

        Observable<LoginResponse> resultObservable = mtvConnectApi.login(request);

        subscribeForNetwork(resultObservable, apiObserver);
    }

}
