package com.mtvindia.connect.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.app.di.RootModule;
import com.mtvindia.connect.ui.custom.pushNotification.OneSignalBroadCastReceiver;
import com.onesignal.OneSignal;

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
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new OneSignalBroadCastReceiver())
                .init();

        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

        // Create module to make it ready for the injection
        Injector.instance().createModule(new RootModule(this));

        // Plant Timber tree for Logging
        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
