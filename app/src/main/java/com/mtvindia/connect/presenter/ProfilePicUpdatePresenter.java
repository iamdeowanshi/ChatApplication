package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;

import retrofit.mime.TypedFile;

/**
 * @author Aaditya Deowanshi
 */

public interface ProfilePicUpdatePresenter extends Presenter<PictureUpdateViewInteractor> {

    void updateProfilePic(int id, TypedFile file, String header);

}
