package com.mtvindia.connect.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.facebook.login.LoginManager;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.ui.fragment.AboutFragment;
import com.mtvindia.connect.ui.fragment.ChatFragment;
import com.mtvindia.connect.ui.fragment.FindMorePeopleFragment;
import com.mtvindia.connect.ui.fragment.NavigationDrawerFragment;
import com.mtvindia.connect.ui.fragment.PreferenceFragment;
import com.mtvindia.connect.ui.fragment.ProfileFragment;

import butterknife.Bind;

public class NavigationActivity extends BaseActivity implements NavigationCallBack {

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @Bind(R.id.drawer)
    DrawerLayout drawerLayout;

    private NavigationDrawerFragment navigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_logo);
        getSupportActionBar().setTitle(null);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragment.initDrawer(R.id.fragment_drawer, drawerLayout, toolbar);

        onItemSelected(NavigationItem.PREFERENCE);
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
        Fragment fragment;
        Bundle bundle = new Bundle();

        switch (item) {
            case FIND_PEOPLE:
                fragment = FindMorePeopleFragment.getInstance(bundle);
                addFragment(fragment);
                break;
            case PROFILE:
                fragment = ProfileFragment.getInstance(bundle);
                addFragment(fragment);
                break;
            case PREFERENCE:
                fragment = PreferenceFragment.getInstance(bundle);
                addFragment(fragment);
                break;
            case CHAT:
                fragment = ChatFragment.getInstance(bundle);
                addFragment(fragment);
                break;
            case ABOUT:
                fragment = AboutFragment.getInstance(bundle);
                addFragment(fragment);
                break;
            case LOGOUT:
                LoginManager.getInstance().logOut();
                finish();
                break;
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

}
