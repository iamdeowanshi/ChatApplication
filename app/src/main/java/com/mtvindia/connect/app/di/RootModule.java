package com.mtvindia.connect.app.di;

import android.content.Context;

import com.mtvindia.connect.app.MtvConnectApplication;
import com.mtvindia.connect.presenter.concrete.LoginPresenterImpl;
import com.mtvindia.connect.presenter.concrete.QuestionRequestPresenterImpl;
import com.mtvindia.connect.presenter.concrete.UpdatePresenterImpl;
import com.mtvindia.connect.ui.activity.LaunchActivity;
import com.mtvindia.connect.ui.activity.LoginActivity;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.fragment.AnswerFragment;
import com.mtvindia.connect.ui.fragment.ChatFragment;
import com.mtvindia.connect.ui.fragment.ChooseFragment;
import com.mtvindia.connect.ui.fragment.NavigationDrawerFragment;
import com.mtvindia.connect.ui.fragment.PreferenceFragment;
import com.mtvindia.connect.ui.fragment.PrimaryQuestionFragment;
import com.mtvindia.connect.ui.fragment.ProfileFragment;
import com.mtvindia.connect.ui.fragment.SecondaryQuestionFragment;
import com.mtvindia.connect.util.PreferenceUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Include all other modules, provide Context dependency. All activity, fragment, presenter and
 * any classes that are going to use dependency injection should be registered here.
 *
 * @author Farhan Ali
 */
@Module(
        includes = {
                PresenterModule.class,
                UtilModule.class,
                ApiModule.class
        },
        injects = {
                MtvConnectApplication.class,

                // View specific classes - activities, fragments, adapters etc
                NavigationDrawerFragment.class,
                NavigationActivity.class,
                LoginActivity.class,
                LaunchActivity.class,
                LoginPresenterImpl.class,
                UpdatePresenterImpl.class,
                QuestionRequestPresenterImpl.class,
                PreferenceFragment.class,
                ProfileFragment.class,
                PrimaryQuestionFragment.class,
                SecondaryQuestionFragment.class,
                ChatFragment.class,
                ChooseFragment.class,
                AnswerFragment.class,

                // Util classes
                PreferenceUtil.class

        }
)
public class RootModule {

    private Context context;

    public RootModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return context;
    }

}
