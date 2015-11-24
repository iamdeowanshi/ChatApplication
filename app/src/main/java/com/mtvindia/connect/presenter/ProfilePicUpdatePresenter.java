package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;

import retrofit.mime.TypedFile;

/**
 * Created by Sibi on 21/11/15.
 */
public interface ProfilePicUpdatePresenter extends Presenter<PictureUpdateViewInteractor> {

    void updateProfilePic(int id, TypedFile file, String header);
}
