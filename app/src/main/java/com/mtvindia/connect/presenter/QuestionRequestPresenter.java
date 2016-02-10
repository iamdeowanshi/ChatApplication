package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;

/**
 * @author Aaditya Deowanshi
 */

public interface QuestionRequestPresenter extends Presenter<QuestionViewInteractor> {

    void getPrimaryQuestion(String header);

    void getSecondaryQuestion(int questionId, String header);

}
