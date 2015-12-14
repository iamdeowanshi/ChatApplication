package com.mtvindia.connect.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;
import com.mtvindia.connect.services.SmackService;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.ui.activity.ChatCallBack;
import com.mtvindia.connect.ui.adapter.ChatListAdapter;
import com.mtvindia.connect.ui.custom.UbuntuTextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sibi on 16/10/15.
 */
public class ChatListFragment extends BaseFragment implements ChatCallBack, DataChangeListener {

    @Inject
    ChatListRepository chatListRepository;

    @Bind(R.id.chat_list)
    RecyclerView userList;
    @Bind(R.id.empty_view)
    UbuntuTextView emptyView;
    private List<ChatList> chatList = new ArrayList<>();
    private ChatMessage chatMessage;
    private ChatListAdapter chatListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_list_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        userList.setLayoutManager(layoutManager);
        userList.setHasFixedSize(true);
        chatList = chatListRepository.sortList();


        chatListAdapter = new ChatListAdapter(this.getContext(), chatList);
        chatListAdapter.setChatCallBack(this);
        userList.setAdapter(chatListAdapter);
        emptyView();

        userList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

    }

    private void emptyView() {
        if (chatList.isEmpty()) {
            userList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            userList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //getContext().startService(new Intent(getContext(), SmackService.class));
       /* String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getContext().getSystemService(ns);
*/
    }

    @Override
    public void onPause() {
        super.onPause();
        //disconnectChatServer();
    }

    private void disconnectChatServer() {
        Intent intent = new Intent(getContext(), SmackService.class);
        getContext().stopService(intent);
    }

    public static Fragment getInstance(Bundle bundle) {
        ChatListFragment chatListFragment = new ChatListFragment();
        chatListFragment.setArguments(bundle);

        return chatListFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemSelected(ChatList chatList) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", chatList.getId());
        startActivity(ChatActivity.class, bundle);
        getActivity().finish();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.chat).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onChange(List updatedData) {
        chatListAdapter.notifyDataSetChanged();
        emptyView();
    }

    @Override
    public void onStatusChanged(String status) {

    }
}
