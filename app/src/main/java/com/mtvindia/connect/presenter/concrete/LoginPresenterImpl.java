package com.mtvindia.connect.presenter.concrete;

import com.google.gson.Gson;
import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.LoginRequest;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.LoginPresenter;
import com.mtvindia.connect.presenter.LoginViewInteractor;

import javax.inject.Inject;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rx.Observable;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class LoginPresenterImpl extends BaseNetworkPresenter<LoginViewInteractor>
        implements LoginPresenter {

    @Inject MtvConnectApi mtvConnectApi;
    @Inject Gson gson;

    public LoginPresenterImpl() {
        injectDependencies();
    }

    private ApiObserver<Response> apiObserver = new ApiObserver<Response>() {
        @Override
        public void onResult(Response response) {
            viewInteractor.hideProgress();

            boolean isRegister = response.getStatus() == 201;

            String userJson = new String(((TypedByteArray) response.getBody()).getBytes());

            Timber.d(userJson);

            User user = gson.fromJson(userJson, User.class);

            viewInteractor.loginDone(user, isRegister);
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

        Observable<Response> resultObservable = mtvConnectApi.login(request);
        Timber.d(gson.toJson(request));

        subscribeForNetwork(resultObservable, apiObserver);
    }

}
