package com.mtvindia.connect.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.presenter.SamplePresenter;
import com.mtvindia.connect.presenter.SampleViewInteractor;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class SampleActivity extends BaseActivity implements SampleViewInteractor {

    @Inject SamplePresenter presenter;

    @Bind(R.id.btn_do_something) Button btnDoSomething;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // call to inject dependencies
        injectDependencies();

        presenter.setViewInteractor(this);
    }

    @OnClick(R.id.btn_do_something)
    void doSomething() {
        presenter.doSomething();
    }

    @Override
    public void showSomeMessage(String message) {
        toastShort(message);
    }

}
