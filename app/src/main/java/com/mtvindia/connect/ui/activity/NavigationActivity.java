package com.mtvindia.connect.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.ui.fragment.NavigationDrawerFragment;
import com.mtvindia.connect.ui.fragment.PreferenceFragment;

import java.util.List;

import butterknife.Bind;

public class NavigationActivity extends BaseActivity implements NavigationCallBacks {

    @Bind(R.id.toolbar_actionbar) Toolbar toolbar;
    @Bind(R.id.drawer) DrawerLayout drawerLayout;

    private NavigationDrawerFragment navigationDrawerFragment;
    private List<NavigationItem> drawerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_logo);
        getSupportActionBar().setTitle(null);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerItems = NavigationItem.getNavigationListItems();
        navigationDrawerFragment.initDrawer(R.id.fragment_drawer, drawerLayout, toolbar);

        onItemSelected(0);
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
    public void onItemSelected(int position) {
        if (drawerItems == null || drawerItems.size() == 0) {
            return;
        }
        Bundle bundle = new Bundle();

         Fragment fragment = PreferenceFragment.getInstance(bundle);
        addFragment(fragment);
    }

    private void addFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
    }

}
