package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.ViewInteractor;
import com.mtvindia.connect.data.model.User;

/**
 * @author Aaditya Deowanshi
 */

public interface PictureUpdateViewInteractor extends ViewInteractor{

    void showPicUpdateProgress();

    void hidePicUpdateProgress();

    void onPicUpdateError(Throwable throwable);

    void showUpdatedPic(User user );

}
