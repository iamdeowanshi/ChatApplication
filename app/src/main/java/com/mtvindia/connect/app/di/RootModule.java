package com.mtvindia.connect.app.di;

import android.content.Context;

import com.mtvindia.connect.app.MtvConnectApplication;
import com.mtvindia.connect.data.repository.realm.BaseRepositoryRealm;
import com.mtvindia.connect.data.repository.realm.ChatListRepositoryRealm;
import com.mtvindia.connect.data.repository.realm.ChatMessageRepositoryRealm;
import com.mtvindia.connect.presenter.concrete.ChatListPresenterImpl;
import com.mtvindia.connect.services.SmackConnection;
import com.mtvindia.connect.services.XmppReciever;
import com.mtvindia.connect.presenter.concrete.AboutUserPresenterImpl;
import com.mtvindia.connect.presenter.concrete.FindMatchPresenterImpl;
import com.mtvindia.connect.presenter.concrete.LoginPresenterImpl;
import com.mtvindia.connect.presenter.concrete.ProfilePicUpdatePresenterImpl;
import com.mtvindia.connect.presenter.concrete.QuestionRequestPresenterImpl;
import com.mtvindia.connect.presenter.concrete.ResultPresenterImpl;
import com.mtvindia.connect.presenter.concrete.UpdatePresenterImpl;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.ui.activity.LaunchActivity;
import com.mtvindia.connect.ui.activity.LoginActivity;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.adapter.ChatListAdapter;
import com.mtvindia.connect.ui.adapter.ChatMessageAdapter;
import com.mtvindia.connect.ui.adapter.NavigationDrawerAdapter;
import com.mtvindia.connect.ui.custom.gcm.GcmIntentService;
import com.mtvindia.connect.ui.fragment.DisplayUserFragment;
import com.mtvindia.connect.ui.fragment.ChatListFragment;
import com.mtvindia.connect.ui.fragment.ChooseFragment;
import com.mtvindia.connect.ui.fragment.NavigationDrawerFragment;
import com.mtvindia.connect.ui.fragment.PreferenceFragment;
import com.mtvindia.connect.ui.fragment.PrimaryQuestionFragment;
import com.mtvindia.connect.ui.fragment.ProfileFragment;
import com.mtvindia.connect.ui.fragment.ResultFragment;
import com.mtvindia.connect.ui.fragment.SecondaryQuestionFragment;
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.NetworkUtil;
import com.mtvindia.connect.util.PreferenceUtil;
import com.mtvindia.connect.util.QuestionPreference;
import com.mtvindia.connect.util.UserPreference;

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
                ApiModule.class,
                OrmModule.class
        },
        injects = {
                MtvConnectApplication.class,
                BaseRepositoryRealm.class,
                SmackConnection.class,
                GcmIntentService.class,
                // View specific classes - activities, fragments, adapters etc

                LaunchActivity.class,
                LoginActivity.class,
                NavigationDrawerFragment.class,
                NavigationDrawerAdapter.class,
                NavigationActivity.class,
                PreferenceFragment.class,
                ProfileFragment.class,
                PrimaryQuestionFragment.class,
                SecondaryQuestionFragment.class,
                ChatListFragment.class,
                ChatActivity.class,
                ChooseFragment.class,
                ResultFragment.class,
                DisplayUserFragment.class,
                ChatMessageAdapter.class,

                LoginPresenterImpl.class,
                UpdatePresenterImpl.class,
                QuestionRequestPresenterImpl.class,
                ResultPresenterImpl.class,
                FindMatchPresenterImpl.class,
                AboutUserPresenterImpl.class,
                ProfilePicUpdatePresenterImpl.class,
                ChatListPresenterImpl.class,
                DisplayUserFragment.class,
                ChatActivity.class,
                ChatListFragment.class,

                ChatListAdapter.class,

                //Realm
                BaseRepositoryRealm.class,
                ChatListRepositoryRealm.class,
                ChatMessageRepositoryRealm.class,

                XmppReciever.class,

                // Util classes
                NetworkUtil.class,
                DialogUtil.class,
                UserPreference.class,
                QuestionPreference.class,
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
