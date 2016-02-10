package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.AboutUser;

/**
 * @author Aaditya Deowanshi
 */

public interface AboutUserViewInteractor extends ViewInteractor {

    void showProgress();

    void hideProgress();

    void onError(Throwable throwable);

    void aboutUser(AboutUser aboutUser);

}
