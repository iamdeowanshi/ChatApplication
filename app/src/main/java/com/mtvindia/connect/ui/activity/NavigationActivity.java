package com.mtvindia.connect.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mtvindia.connect.R;
import com.mtvindia.connect.ui.fragment.NavigationDrawerFragment;

import java.util.List;

public class NavigationActivity extends AppCompatActivity implements NavigationCallBacks {

    private NavigationDrawerFragment navigationDrawerFragment;
    private Toolbar toolbar;
    private List<NavigationItem> drawerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.action_bar_icon);
        getSupportActionBar().setTitle(null);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerItems = NavigationItem.getNavigationListItems();
        navigationDrawerFragment.initDrawer(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);

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
        // Fragment fragment = NavigationDrawerFragment.getInstance(bundle);
        //addFragment(fragment);
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
