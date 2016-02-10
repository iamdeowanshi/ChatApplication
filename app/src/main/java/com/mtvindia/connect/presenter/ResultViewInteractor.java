package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.ResultResponse;

/**
 * @author Aaditya Deowanshi
 */

public interface ResultViewInteractor extends ViewInteractor {

    void showProgress();

    void hideProgress();

    void showResult(ResultResponse response);

    void onError(Throwable throwable);

}
