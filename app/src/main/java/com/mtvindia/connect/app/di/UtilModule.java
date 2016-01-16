package com.mtvindia.connect.app.di;

import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.NetworkUtil;
import com.mtvindia.connect.util.PermissionUtil;
import com.mtvindia.connect.util.PreferenceUtil;
import com.mtvindia.connect.util.QuestionPreference;
import com.mtvindia.connect.util.UserPreference;
import com.mtvindia.connect.util.ViewUtil;

import javax.inject.Singleton;

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
public class UtilModule {

    @Provides
    @Singleton
    public PreferenceUtil providePreferenceUtil() {
        return new PreferenceUtil();
    }

    @Provides
    @Singleton
    public NetworkUtil provideNetworkUtil() {
        return new NetworkUtil();
    }

    @Provides
    @Singleton
    public DialogUtil provideDialogUtil() {
        return new DialogUtil();
    }

    @Provides
    public UserPreference provideUserPreference() {
        return new UserPreference();
    }

    @Provides
    public QuestionPreference provideQuestionPreference() {
        return new QuestionPreference();
    }

    @Provides
    public ViewUtil provideViewUtil() {
        return new ViewUtil();
    }

    @Provides PermissionUtil providePermissionUtil() {
        return new PermissionUtil();
    }

}
