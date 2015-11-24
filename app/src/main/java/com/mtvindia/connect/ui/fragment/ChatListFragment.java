package com.mtvindia.connect.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;

/**
 * Created by Sibi on 16/10/15.
 */
public class ChatListFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_list_fragment, container, false);


        return view;
    }

    public static Fragment getInstance(Bundle bundle) {
        ChatListFragment chatListFragment = new ChatListFragment();
        chatListFragment.setArguments(bundle);

        return chatListFragment;
    }

}
