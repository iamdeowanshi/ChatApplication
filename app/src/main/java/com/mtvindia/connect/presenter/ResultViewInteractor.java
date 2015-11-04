package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.ResultResponse;

/**
 * Created by Sibi on 04/11/15.
 */
public interface ResultViewInteractor extends ViewInteractor {

    void showProgress();

    void hideProgress();

    void showResult(ResultResponse response);

    void onError(Throwable throwable);
}
