package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.Question;

/**
 * Created by Sibi on 02/11/15.
 */
public interface QuestionViewInteractor extends ViewInteractor {

    void showProgress();

    void hideProgress();

    void display(Question question);

    void onError(Throwable throwable);

}
