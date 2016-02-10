package com.mtvindia.connect.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;
import com.mtvindia.connect.services.SmackService;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.ui.adapter.ChatListAdapter;
import com.mtvindia.connect.ui.callbacks.ChatCallBack;
import com.mtvindia.connect.util.UserPreference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Aaditya Deowanshi
 *
 *         Chat list screen, where user can see all active chats.
 */

public class ChatListFragment extends BaseFragment implements ChatCallBack, DataChangeListener {

    @Inject ChatListRepository chatListRepository;
    @Inject UserPreference userPreference;

    @Bind(R.id.chat_list) RecyclerView userList;
    @Bind(R.id.empty_view) ImageView emptyView;

    private ChatListAdapter chatListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkPurple));
        }

        userPreference.removePushMessage();
        userList.setLayoutManager(layoutManager);
        userList.setHasFixedSize(true);

        List<ChatList> chatList = chatListRepository.sortList(userPreference.readUser().getId());
        chatListRepository.setDataChangeListener(ChatListFragment.this);


        chatListAdapter = new ChatListAdapter(getContext(), chatList);
        chatListAdapter.setChatCallBack(this);
        userList.setAdapter(chatListAdapter);

        toggleEmptyView(chatList);
    }

    @Override
    public void onResume() {
        super.onResume();
        chatListAdapter.notifyDataSetChanged();
        getContext().startService(new Intent(getContext(), SmackService.class));
    }

    @Override
    public void onPause() {
        super.onPause();
        disconnectChatServer();
    }

    @Override
    public void onItemSelected(ChatList chatList) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", chatList.getUserId());
        userPreference.saveSelectedUser(chatList.getUserId());
        startActivity(ChatActivity.class, bundle);
        getActivity().finish();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.chat).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onRealmDataChange(List updatedData) {
        chatListAdapter.notifyDataSetChanged();
        toggleEmptyView(chatListRepository.sortList(userPreference.readUser().getId()));
    }

    @Override
    public void onStatusChange(String status) {}

    /**
     * Disconnect to chat server.
     */
    private void disconnectChatServer() {
        Intent intent = new Intent(getContext(), SmackService.class);
        getContext().stopService(intent);
    }

    /**
     * Toggle between empty view and chat list.
     * If no active chat is present, empty view will be shown.
     */
    private void toggleEmptyView(List<ChatList> chatList) {
        if (chatList.isEmpty()) {
            userList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

            return;
        }

        userList.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    public static Fragment getInstance(Bundle bundle) {
        ChatListFragment chatListFragment = new ChatListFragment();
        chatListFragment.setArguments(bundle);

        return chatListFragment;
    }

}
