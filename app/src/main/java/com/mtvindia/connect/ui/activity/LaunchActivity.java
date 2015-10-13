package com.mtvindia.connect.ui.activity;

import android.os.Bundle;
import android.os.Handler;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;

public class LaunchActivity extends BaseActivity {

    private static int timeOut = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(LoginActivity.class, null);
            }
        }, timeOut);
    }

}
