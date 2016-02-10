package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;
import com.mtvindia.connect.data.model.User;

/**
 * @author Aaditya Deowanshi
 */

public interface UpdatePresenter extends Presenter<UpdateViewInteractor> {

    void update(User user);

    void updateLocation(User user);

}
