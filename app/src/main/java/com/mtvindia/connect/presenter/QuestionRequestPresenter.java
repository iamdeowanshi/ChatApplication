package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;

/**
 * Created by Sibi on 02/11/15.
 */
public interface QuestionRequestPresenter extends Presenter<QuestionViewInteractor> {

    void getPrimaryQuestion(String header);

    void getSecondaryQuestion(int questionId, String header);

}
