package com.mtvindia.connect.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.activity.NavigationItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sibi on 14/10/15.
 */
public class PreferenceFragment extends BaseFragment {

    @Bind(R.id.layout_dialog_interested)
    RelativeLayout layoutDialogInterested;
    @Bind(R.id.layout_dialog_meet)
    RelativeLayout layoutDialogMeet;
    @Bind(R.id.btn_save)
    Button save;
    @Bind(R.id.txt_interested)
    TextView textInterested;
    @Bind(R.id.txt_meet)
    TextView textMeet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        layoutDialogInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(" Pick interested in:");
                builder.setIcon(R.drawable.icon_interested);
                builder.setItems(R.array.interested_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] interested = getResources().getStringArray(R.array.interested_array);
                        textInterested.setText(interested[which]);
                    }
                });
                builder.show();
            }
        });

        layoutDialogMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(" Meet :");
                builder.setIcon(R.drawable.icon_meet);
                builder.setItems(R.array.meet_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] meet = getResources().getStringArray(R.array.meet_array);
                        textMeet.setText(meet[which]);
                    }
                });
                builder.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavigationActivity navigationActivity = (NavigationActivity) getContext();
                Fragment fragment = ProfileFragment.getInstance(null);
                navigationActivity.addFragment(fragment);

                NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
                navigationDrawerFragment.onItemSelected(NavigationItem.PROFILE);
            }
        });
    }

    public static Fragment getInstance(Bundle bundle) {
        PreferenceFragment preferenceFragment = new PreferenceFragment();
        preferenceFragment.setArguments(bundle);

        return preferenceFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
