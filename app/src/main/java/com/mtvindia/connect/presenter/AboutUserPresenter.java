package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;

/**
 * Created by Sibi on 20/11/15.
 */
public interface AboutUserPresenter extends Presenter<AboutUserViewInteractor> {

    void getAboutUser(int id, String header);
}
