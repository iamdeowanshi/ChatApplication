package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;
import com.mtvindia.connect.data.model.ResultRequest;

/**
 * Created by Sibi on 04/11/15.
 */
public interface ResultPresenter extends Presenter<ResultViewInteractor> {

    void requestResult(ResultRequest resultRequest, String accessToken);
}
