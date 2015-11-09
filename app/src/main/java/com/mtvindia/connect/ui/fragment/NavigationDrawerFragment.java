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
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.ui.activity.NavigationCallBack;
import com.mtvindia.connect.ui.activity.NavigationItem;
import com.mtvindia.connect.ui.adapter.NavigationDrawerAdapter;
import com.mtvindia.connect.ui.custom.CircleStrokeTransformation;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.UserPreference;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sibi on 13/10/15.
 */
public class NavigationDrawerFragment extends BaseFragment implements NavigationCallBack {

    @Inject UserPreference userPreference;

    @Bind(R.id.recycler_view)
    RecyclerView drawerList;
    @Bind(R.id.img_dp)
    ImageView imgDp;
    @Bind(R.id.txt_item_name)
    UbuntuTextView txtItemName;

    private View fragmentContainerView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private List<NavigationItem> drawerItems;
    private NavigationCallBack navigationCallBack;

    private CircleStrokeTransformation circleStrokeTransformation;

    private User user;
    private NavigationItem selectedItem;

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

        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), android.R.color.transparent, 1);

        user = userPreference.readUser();

        drawerItems = Arrays.asList(NavigationItem.values());
        drawerList.setLayoutManager(layoutManager);
        drawerList.setHasFixedSize(true);

        txtItemName.setText(user.getFirstName() + " " + user.getLastName());
        Picasso.with(getContext()).load(user.getProfilePic()).transform(circleStrokeTransformation).into(imgDp);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(drawerItems);
        adapter.setNavigationCallbacks(this);
        drawerList.setAdapter(adapter);
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

    @Override
    public void onResume() {
        super.onResume();

        user = userPreference.readUser();

        txtItemName.setText(user.getFirstName() + " " + user.getLastName());
        Picasso.with(getContext()).load(user.getProfilePic()).transform(circleStrokeTransformation).into(imgDp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setActionBarDrawerToggleEnabled(boolean isEnable) {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(isEnable);
    }

}
