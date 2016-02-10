package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.User;

/**
 * @author Aaditya Deowanshi
 */

public interface UpdateViewInteractor extends ViewInteractor {

    void showProgress();

    void hideProgress();

    void updateDone(User user);

    void onError(Throwable throwable);

}
