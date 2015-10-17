package com.mtvindia.connect.ui.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.ui.activity.NavigationCallBack;
import com.mtvindia.connect.ui.activity.NavigationItem;
import com.mtvindia.connect.ui.adapter.NavigationDrawerAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Sibi on 13/10/15.
 */
public class NavigationDrawerFragment extends BaseFragment implements NavigationCallBack {

    @Bind(R.id.recycler_view)
    RecyclerView drawerList;

    private View fragmentContainerView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private List<NavigationItem> drawerItems;
    private NavigationCallBack navigationCallBack;

    private NavigationItem selectedItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        drawerItems = Arrays.asList(NavigationItem.values());
        drawerList.setLayoutManager(layoutManager);
        drawerList.setHasFixedSize(true);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(drawerItems);
        adapter.setNavigationCallbacks(this);
        drawerList.setAdapter(adapter);

        onItemSelected(NavigationItem.PREFERENCE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            navigationCallBack = (NavigationCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationCallBack = null;
    }


    public void initDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;

        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), NavigationDrawerFragment.this.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();

                if (navigationCallBack != null) {
                    navigationCallBack.onItemSelected(selectedItem);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        this.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(fragmentContainerView);
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemSelected(NavigationItem item) {

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(fragmentContainerView);
        }

        selectedItem = item;

        if (navigationCallBack != null) {
            navigationCallBack.onItemSelected(item);
        }

        ((NavigationDrawerAdapter) drawerList.getAdapter()).setSelectedItem(item);
    }


}
