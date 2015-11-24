package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.AboutUser;

/**
 * Created by Sibi on 20/11/15.
 */
public interface AboutUserViewInteractor extends ViewInteractor {

    void showProgress();

    void hideProgress();

    void onError(Throwable throwable);

    void aboutUser(AboutUser aboutUser);
}
