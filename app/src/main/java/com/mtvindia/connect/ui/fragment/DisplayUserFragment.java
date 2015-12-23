package com.mtvindia.connect.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.Config;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.AboutUser;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.presenter.AboutUserPresenter;
import com.mtvindia.connect.presenter.AboutUserViewInteractor;
import com.mtvindia.connect.services.SmackService;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.UserPreference;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Sibi on 20/11/15.
 */
public class DisplayUserFragment extends BaseFragment implements AboutUserViewInteractor {

    @Inject
    AboutUserPresenter presenter;
    @Inject
    UserPreference userPreference;
    @Inject
    ChatListRepository chatListRepository;
    @Inject
    ChatMessageRepository chatMessageRepository;

    @Bind(R.id.img_dp)
    ImageView imgDp;
    @Bind(R.id.txt_name)
    UbuntuTextView txtName;
    @Bind(R.id.txt_about)
    UbuntuTextView txtAbout;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.txt_common)
    UbuntuTextView txtCommon;
    @Bind(R.id.edt_message)
    EmojiconEditText edtMessage;
    @Bind(R.id.icon_send)
    ImageView iconSend;

    private int userId;
    private ChatList chatList = new ChatList();
    private ChatMessage chatMessage = new ChatMessage();
    private String message;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        getContext().startService(new Intent(getContext(), SmackService.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispaly_user_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
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

        presenter.setViewInteractor(this);

        userId = getArguments().getInt("UserId");

        User user = userPreference.readUser();

        presenter.getAboutUser(userId, user.getAuthHeader());

    }

    void sendMessage(AboutUser selectedUser) {
        message = edtMessage.getText().toString().trim();

        if (message.equals("")) {
            edtMessage.setText("");
            return;
        }
            DateTime time = DateTime.now();
            Timber.d(time.toString());
            chatMessage.setFrom("webuser" + userPreference.readUser().getId());
            chatMessage.setTo("webuser" + selectedUser.getId());
            chatMessage.setStatus("sent");
            chatMessage.setUserId(selectedUser.getId());
            chatMessage.setCreatedTime(time.toString());
            chatMessage.setBody(edtMessage.getText().toString());
            chatMessage.setCreatedTime(time.toString());
            chatMessageRepository.save(chatMessage);
            chatListRepository.updateTime(userId, userPreference.readUser().getId(), time.toString());

            sendToChatServer();
        
            Bundle bundle = new Bundle();
            bundle.putInt("userId", chatList.getUserId());
            startActivity(ChatActivity.class, bundle);
            getActivity().finish();


        edtMessage.setText("");
    }


    public static Fragment getInstance(Bundle bundle) {
        DisplayUserFragment displayUserFragment = new DisplayUserFragment();
        displayUserFragment.setArguments(bundle);

        return displayUserFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onError(Throwable throwable) {
        Timber.e(throwable, "Error");
        toastShort("Error: " + throwable);
        hideProgress();
    }

    private void sendToChatServer() {
        Intent intent = new Intent(SmackService.SEND_MESSAGE);
        intent.setPackage(getContext().getPackageName());
        intent.putExtra(SmackService.BUNDLE_MESSAGE_BODY, message);
        intent.putExtra(SmackService.BUNDLE_TO, chatMessage.getTo() + "@" + Config.CHAT_SERVER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        }
        getContext().sendBroadcast(intent);
    }

    @Override
    public void aboutUser(final AboutUser aboutUser) {
        chatList.setUserId(aboutUser.getId());
        chatList.setImage(aboutUser.getProfilePic());
        chatList.setLogedinUser(userPreference.readUser().getId());
        chatList.setName(aboutUser.getFullName());

        txtName.setText(aboutUser.getFullName());
        txtAbout.setText(aboutUser.getAbout());
        txtCommon.setText("You and " + aboutUser.getFirstName() + " have " + aboutUser.getCommonThingsCount() + " things in common");

        Picasso.with(getContext()).load(aboutUser.getProfilePic()).fit().into(imgDp);

        iconSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(aboutUser);
            }
        });
    }
}
