package com.mtvindia.connect.ui.activity;

import android.os.Bundle;
import android.os.Handler;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.util.UserPreference;

import javax.inject.Inject;

public class LaunchActivity extends BaseActivity {

    @Inject UserPreference userPreference;

    private static int timeOut = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        injectDependencies();

        proceed();
    }

    public void proceed() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userPreference.readUser() == null) {
                    startActivity(LoginActivity.class, null);
                } else {
                    startActivity(NavigationActivity.class, null);
                }
                finish();
            }
        }, timeOut);
    }

}
