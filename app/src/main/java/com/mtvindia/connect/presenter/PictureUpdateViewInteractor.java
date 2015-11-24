package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.User;

/**
 * Created by Sibi on 21/11/15.
 */
public interface PictureUpdateViewInteractor extends ViewInteractor{

    void showPicUpdateProgress();

    void hidePicUpdateProgress();

    void onPicUpdateError(Throwable throwable);

    void showUpdatedPic(User user );
}
