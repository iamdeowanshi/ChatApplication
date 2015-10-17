package com.mtvindia.connect.app.di;

import android.content.Context;

import com.mtvindia.connect.app.MtvConnectApplication;
import com.mtvindia.connect.ui.fragment.NavigationDrawerFragment;

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
                ApiModule.class
        },
        injects = {
                MtvConnectApplication.class,

                // View specific classes - activities, fragments, adapters etc
                NavigationDrawerFragment.class,

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
