package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.Question;

/**
 * @author Aaditya Deowanshi
 */

public interface QuestionViewInteractor extends ViewInteractor {

    void showProgress();

    void hideProgress();

    void showQuestion(Question question);

    void onError(Throwable throwable);

}
