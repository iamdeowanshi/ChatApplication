package com.mtvindia.connect.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by Sibi on 16/10/15.
 */
public class FindMorePeopleFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_people, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    public static Fragment getInstance(Bundle bundle) {
        FindMorePeopleFragment findMorePeopleFragment = new FindMorePeopleFragment();
        findMorePeopleFragment.setArguments(bundle);

        return findMorePeopleFragment;
    }

}
