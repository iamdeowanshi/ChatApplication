package com.mtvindia.connect.ui.activity;

import android.os.Bundle;
import android.os.Handler;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;

import butterknife.ButterKnife;

public class LaunchActivity extends BaseActivity {

    private static int timeOut = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(LoginActivity.class, null);
            }

        }, timeOut);
    }

}
