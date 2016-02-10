package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;
import com.mtvindia.connect.data.model.ResultRequest;

/**
 * @author Aaditya Deowanshi
 */

public interface ResultPresenter extends Presenter<ResultViewInteractor> {

    void requestResult(ResultRequest resultRequest, String accessToken);

}
