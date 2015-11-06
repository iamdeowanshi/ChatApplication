package com.mtvindia.connect.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.data.model.Question;
import com.mtvindia.connect.ui.fragment.AboutFragment;
import com.mtvindia.connect.ui.fragment.ChatFragment;
import com.mtvindia.connect.ui.fragment.NavigationDrawerFragment;
import com.mtvindia.connect.ui.fragment.PreferenceFragment;
import com.mtvindia.connect.ui.fragment.PrimaryQuestionFragment;
import com.mtvindia.connect.ui.fragment.ProfileFragment;
import com.mtvindia.connect.ui.fragment.ResultFragment;
import com.mtvindia.connect.ui.fragment.SecondaryQuestionFragment;
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.NetworkUtil;
import com.mtvindia.connect.util.PreferenceUtil;

import javax.inject.Inject;

import butterknife.Bind;

public class NavigationActivity extends BaseActivity implements NavigationCallBack {

    @Inject PreferenceUtil preferenceUtil;
    @Inject NetworkUtil networkUtil;
    @Inject DialogUtil dialogUtil;

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @Bind(R.id.drawer)
    DrawerLayout drawerLayout;

    private NavigationDrawerFragment navigationDrawerFragment;
    private Fragment fragment;

    static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(internetReciever, filter);

        injectDependencies();

        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.icon_logo);
        getSupportActionBar().setTitle(null);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragment.initDrawer(R.id.fragment_drawer, drawerLayout, toolbar);

        loadInitialItem();
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemSelected(NavigationItem item) {
        switch (item) {
            case FIND_PEOPLE:
                setDrawerEnabled(true);
                showFindMorePeople();
                break;
            case PROFILE:
                fragment = ProfileFragment.getInstance(null);
                addFragment(fragment);
                break;
            case PREFERENCE:
                fragment = PreferenceFragment.getInstance(null);
                addFragment(fragment);
                break;
            case CHAT:
                fragment = ChatFragment.getInstance(null);
                addFragment(fragment);
                break;
            case ABOUT:
                fragment = AboutFragment.getInstance(null);
                addFragment(fragment);
                break;
            case LOGOUT:
                startActivity(LoginActivity.class, null);
                finish();
                break;
        }
    }

    private void showFindMorePeople() {
        int count = preferenceUtil.readInt(PreferenceUtil.QUESTIONS_ANSWERED, 0);
        Question question = (Question) preferenceUtil.read(PreferenceUtil.QUESTION_RESPONSE, Question.class);
        Fragment fragment;
        if(!question.isAnswered()) {
            fragment = SecondaryQuestionFragment.getInstance(null);
            addFragment(fragment);
        } else if(count == 0) {
            fragment = PrimaryQuestionFragment.getInstance(null);
            addFragment(fragment);
        } else if(count > 0){
            fragment = ResultFragment.getInstance(null);
            addFragment(fragment);
        }
    }

    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }

    private void loadInitialItem() {
        boolean isInRegistration = preferenceUtil.readBoolean(PreferenceUtil.IS_IN_REGISTRATION, false);

        if(isInRegistration) {
            setDrawerEnabled(false);
            onItemSelected(NavigationItem.PREFERENCE);
        } else {
            onItemSelected(NavigationItem.FIND_PEOPLE);
        }
    }

    private void setDrawerEnabled(boolean isEnable) {
        navigationDrawerFragment.setActionBarDrawerToggleEnabled(isEnable);

        int drawerLockMode = isEnable ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;

        drawerLayout.setDrawerLockMode(drawerLockMode);
    }


    private final BroadcastReceiver internetReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean isOnline = networkUtil.isOnline();

            if(!isOnline) {
                dialogUtil.displayInternetAlert(NavigationActivity.this);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetReciever);
    }
}
