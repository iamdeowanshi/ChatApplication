package com.mtvindia.connect.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;

import butterknife.Bind;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.btn_fb)
    Button btnFb;
    @Bind(R.id.btn_gPlus)
    Button btnGplus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NavigationActivity.class,  null);
            }
        });
    }

}
