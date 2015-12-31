package com.mtvindia.connect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PopUpActivity extends AppCompatActivity {

    private String message;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        Bundle bundle = getIntent().getExtras();
    }
}
