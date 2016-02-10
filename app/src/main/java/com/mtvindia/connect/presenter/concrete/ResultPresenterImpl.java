package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.ResultRequest;
import com.mtvindia.connect.data.model.ResultResponse;
import com.mtvindia.connect.presenter.ResultPresenter;
import com.mtvindia.connect.presenter.ResultViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Aaditya Deowanshi.
 *
 *         AnswerPresenter to fetch details about selected answer from server.
 */

public class ResultPresenterImpl extends BaseNetworkPresenter<ResultViewInteractor> implements ResultPresenter {

    @Inject MtvConnectApi mtvConnectApi;

    public ResultPresenterImpl() {
        injectDependencies();
    }

    private ApiObserver<ResultResponse> apiObserver = new ApiObserver<ResultResponse>() {
        @Override
        public void onResult(ResultResponse result) {
            viewInteractor.hideProgress();
            viewInteractor.showResult(result);
        }

        @Override
        public void onError(Throwable e) {
            viewInteractor.onError(e);
        }
    };

    @Override
    public void requestResult(ResultRequest resultRequest, String accessToken) {
        viewInteractor.showProgress();
        Observable<ResultResponse> resultObservable = mtvConnectApi.result(resultRequest, accessToken);

        subscribeForNetwork(resultObservable, apiObserver);
    }

}
