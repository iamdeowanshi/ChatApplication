package com.mtvindia.connect.app;

import android.app.Application;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.app.di.RootModule;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class MtvConnectApplication extends Application {

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

        // Create module to make it ready for the injection
        Injector.instance().createModule(new RootModule(this));


        // Plant Timber tree for Logging
        Timber.plant(new Timber.DebugTree());

    }

}
