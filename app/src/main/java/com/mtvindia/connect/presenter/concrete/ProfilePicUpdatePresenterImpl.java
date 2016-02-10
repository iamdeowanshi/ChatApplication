package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.PictureUpdateViewInteractor;
import com.mtvindia.connect.presenter.ProfilePicUpdatePresenter;

import javax.inject.Inject;

import retrofit.mime.TypedFile;
import rx.Observable;

/**
 * @author Aaditya Deowanshi
 *
 *         Presenter class to update profile picture.
 */
public class ProfilePicUpdatePresenterImpl extends BaseNetworkPresenter<PictureUpdateViewInteractor> implements ProfilePicUpdatePresenter {

    @Inject MtvConnectApi mtvConnectApi;

    public ProfilePicUpdatePresenterImpl() {
        injectDependencies();
    }

    private ApiObserver<User> apiObserver = new ApiObserver<User>() {
        @Override
        public void onResult(User result) {
            viewInteractor.hidePicUpdateProgress();
            viewInteractor.showUpdatedPic(result);
        }

        @Override
        public void onError(Throwable e) {
            viewInteractor.onPicUpdateError(e);
        }
    };

    @Override
    public void updateProfilePic(int id, TypedFile file, String header) {
        viewInteractor.showPicUpdateProgress();
        Observable<User> resultObservable = mtvConnectApi.updatePic(id, file, header);

        subscribeForNetwork(resultObservable, apiObserver);
    }

}
