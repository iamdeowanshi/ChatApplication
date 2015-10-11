package com.mtvindia.connect.app;

import android.app.Application;

import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.app.di.RootModule;

import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class MtvConnectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create module to make it ready for the injection
        Injector.instance().createModule(new RootModule(this));

        // Plant Timber tree for Logging
        Timber.plant(new Timber.DebugTree());
    }

}
