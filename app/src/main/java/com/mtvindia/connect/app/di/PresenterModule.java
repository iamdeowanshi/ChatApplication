package com.mtvindia.connect.app.di;

import com.mtvindia.connect.presenter.AboutUserPresenter;
import com.mtvindia.connect.presenter.ChatListPresenter;
import com.mtvindia.connect.presenter.FindMatchPresenter;
import com.mtvindia.connect.presenter.LoginPresenter;
import com.mtvindia.connect.presenter.ProfilePicUpdatePresenter;
import com.mtvindia.connect.presenter.QuestionRequestPresenter;
import com.mtvindia.connect.presenter.ResultPresenter;
import com.mtvindia.connect.presenter.UpdatePresenter;
import com.mtvindia.connect.presenter.concrete.AboutUserPresenterImpl;
import com.mtvindia.connect.presenter.concrete.ChatListPresenterImpl;
import com.mtvindia.connect.presenter.concrete.FindMatchPresenterImpl;
import com.mtvindia.connect.presenter.concrete.LoginPresenterImpl;
import com.mtvindia.connect.presenter.concrete.ProfilePicUpdatePresenterImpl;
import com.mtvindia.connect.presenter.concrete.QuestionRequestPresenterImpl;
import com.mtvindia.connect.presenter.concrete.ResultPresenterImpl;
import com.mtvindia.connect.presenter.concrete.UpdatePresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Provides all presenter class dependencies.
 *
 * @author Farhan Ali
 */
@Module(
        complete = false,
        library = true
)
public class PresenterModule {

    @Provides
    public LoginPresenter provideLoginPresenter() {
        return new LoginPresenterImpl();
    }

    @Provides
    public UpdatePresenter provideUpdatePresenter() {
        return new UpdatePresenterImpl();
    }

    @Provides
    public ProfilePicUpdatePresenter profilePicUpdatePresenter() {
        return new ProfilePicUpdatePresenterImpl();
    }

    @Provides
    public QuestionRequestPresenter provideQuestionRequestPresenter() {
        return new QuestionRequestPresenterImpl();
    }

    @Provides
    public ResultPresenter provideResultPresenter() {
        return new ResultPresenterImpl();
    }

    @Provides
    public FindMatchPresenter provideFindMatchPresenter() {
        return new FindMatchPresenterImpl();
    }

    @Provides
    public AboutUserPresenter provideAboutUserPresenter() {
        return new AboutUserPresenterImpl();
    }

    @Provides
    public ChatListPresenter provideChatListPresenter() {
        return new ChatListPresenterImpl();
    }

}
