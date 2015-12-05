package com.mtvindia.connect.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.ui.activity.ChatCallBack;
import com.mtvindia.connect.ui.adapter.ChatListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sibi on 16/10/15.
 */
public class ChatListFragment extends BaseFragment implements ChatCallBack {

    @Inject
    ChatListRepository chatListRepository;

    @Bind(R.id.chat_list)
    RecyclerView userList;
    private List<ChatList> chatList = new ArrayList<>();
    private ChatMessage chatMessage;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        userList.setLayoutManager(layoutManager);
        userList.setHasFixedSize(true);
        chatList = chatListRepository.sortList();


        ChatListAdapter chatListAdapter = new ChatListAdapter(getContext(), chatList);
        chatListAdapter.setChatCallBack(this);
        userList.setAdapter(chatListAdapter);

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
        bundle.putInt("userId",chatList.getId());
        startActivity(ChatActivity.class, bundle);
        getActivity().finish();
    }

}
