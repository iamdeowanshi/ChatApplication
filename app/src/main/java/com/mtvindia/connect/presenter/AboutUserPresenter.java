package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;

/**
 * @author Aaditya Deowanshi
 */

public interface AboutUserPresenter extends Presenter<AboutUserViewInteractor> {

    void getAboutUser(int id, String header);

}
