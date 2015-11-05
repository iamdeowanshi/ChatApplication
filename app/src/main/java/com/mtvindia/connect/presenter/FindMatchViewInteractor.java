package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.User;

import java.util.List;

/**
 * Created by Sibi on 05/11/15.
 */
public interface FindMatchViewInteractor extends ViewInteractor{

    void showProgress();

    void hideProgress();

    void showUsers(List<User> users);

    void onError(Throwable throwable);
}
