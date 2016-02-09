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
import android.widget.ImageView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.NavigationItem;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.ui.callbacks.NavigationCallBack;
import com.mtvindia.connect.ui.adapter.NavigationDrawerAdapter;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.UserPreference;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Aaditya Deowanshi
 *
 *         Navigation drawer fragment, which displays navigation drawer.
 */

public class NavigationDrawerFragment extends BaseFragment implements NavigationCallBack {

    @Inject UserPreference userPreference;

    @Bind(R.id.recycler_view) RecyclerView drawerList;
    @Bind(R.id.img_dp) ImageView imgDp;
    @Bind(R.id.txt_item_name) UbuntuTextView txtItemName;

    private User user;

    private List<NavigationItem> drawerItems;
    private NavigationCallBack navigationCallBack;

    private DrawerLayout drawerLayout;
    private View fragmentContainerView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_drawer, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        user = userPreference.readUser();

        drawerItems = Arrays.asList(NavigationItem.values());
        drawerList.setLayoutManager(layoutManager);
        drawerList.setHasFixedSize(true);

        txtItemName.setText(user.getFullName());
        Picasso.with(getContext()).load(user.getProfilePic()).fit().into(imgDp);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(drawerItems);
        adapter.setNavigationCallbacks(this);
        drawerList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        return (actionBarDrawerToggle.onOptionsItemSelected(item))
                ? true
                : false;
    }

    @Override
    public void onResume() {
        super.onResume();

        user = userPreference.readUser();

        txtItemName.setText(user.getFullName());
        Picasso.with(getContext()).load(user.getProfilePic()).fit().into(imgDp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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

        if (navigationCallBack != null) {
            navigationCallBack.onItemSelected(item);
        }

        ((NavigationDrawerAdapter) drawerList.getAdapter()).setSelectedItem(item);
    }

    /**
     * Initializing Navigation drawer.
     * @param fragmentId
     * @param drawerLayout
     * @param toolbar
     */
    public void initializeDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;

        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), NavigationDrawerFragment.this.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                user = userPreference.readUser();
                txtItemName.setText(user.getFullName());
                Picasso.with(getContext()).load(user.getProfilePic()).fit().into(imgDp);
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

    /**
     * Method to close navigation drawer.
     */
    public void closeDrawer() {
        drawerLayout.closeDrawer(fragmentContainerView);
    }

    /**
     * Checking whether drawer is open or not.
     * @return
     */
    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    /**
     * Toggling the visibility of navigation drawer.
     * @param isEnable
     */
    public void setActionBarDrawerToggleEnabled(boolean isEnable) {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(isEnable);
    }

}
