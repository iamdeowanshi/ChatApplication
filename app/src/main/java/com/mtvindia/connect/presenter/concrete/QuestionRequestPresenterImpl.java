package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.Question;
import com.mtvindia.connect.presenter.QuestionRequestPresenter;
import com.mtvindia.connect.presenter.QuestionViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Sibi on 02/11/15.
 */
public class QuestionRequestPresenterImpl extends BaseNetworkPresenter<QuestionViewInteractor> implements QuestionRequestPresenter {

    @Inject MtvConnectApi mtvConnectApi;

    public QuestionRequestPresenterImpl() {
        injectDependencies();
    }

    private ApiObserver<Question> apiObserver = new ApiObserver<Question>() {
        @Override
        public void onResult(Question result) {
            viewInteractor.showQuestion(result);
            viewInteractor.hideProgress();

        }

        @Override
        public void onError(Throwable e) {
            viewInteractor.onError(e);
        }
    };

    @Override
    public void getPrimaryQuestion(String header) {
        viewInteractor.showProgress();
        Observable<Question> resultObservable = mtvConnectApi.primaryQuestionRequest(header);

        subscribeForNetwork(resultObservable, apiObserver);
    }

    @Override
    public void getSecondaryQuestion(int pq_id, String header) {
        viewInteractor.showProgress();

        Observable<Question> resultObservable = mtvConnectApi.secondaryQuestionRequest(pq_id, header);

        subscribeForNetwork(resultObservable, apiObserver);
    }

}
