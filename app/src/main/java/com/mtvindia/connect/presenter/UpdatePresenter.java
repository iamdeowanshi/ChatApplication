package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;
import com.mtvindia.connect.data.model.User;

/**
 * Created by Sibi on 28/10/15.
 */
public interface UpdatePresenter extends Presenter<UpdateViewInteractor> {

    void update(User user);

    void updateLocation(User user);
}
