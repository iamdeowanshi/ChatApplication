package com.mtvindia.connect.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
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
    @Bind(R.id.img_smiley)
    ImageView imgSmiley;
    @Bind(R.id.root_view)
    RelativeLayout rootView;

    private int userId;
    private ChatList chatList = new ChatList();
    private ChatMessage chatMessage = new ChatMessage();
    private String message;
    private EmojiconsPopup popup;

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

        setEmojicons();

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
            sendToChatServer();

            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);
            startActivity(ChatActivity.class, bundle);
            getActivity().finish();
        chatListRepository.updateTime(userId, userPreference.readUser().getId(), DateTime.now().toString());
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
        intent.putExtra(SmackService.BUNDLE_TO, "webuser" + userId + "@" + Config.CHAT_SERVER);

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

    private void setEmojicons() {
        popup = new EmojiconsPopup(rootView, getActivity());
        popup.setSizeForSoftKeyboard();

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(imgSmiley, R.drawable.icon_keyboard);
            }
        });

        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
                changeEmojiKeyboardIcon(imgSmiley, R.drawable.icon_smiley);
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (edtMessage == null || emojicon == null) {
                    return;
                }

                int start = edtMessage.getSelectionStart();
                int end = edtMessage.getSelectionEnd();
                if (start < 0) {
                    edtMessage.append(emojicon.getEmoji());
                } else {
                    edtMessage.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                edtMessage.dispatchKeyEvent(event);
            }
        });

    }


    @OnClick(R.id.img_smiley)
    void onClickSmiley() {

        if (!popup.isShowing()) {

            //If keyboard is visible, simply show the emoji popup
            if (popup.isKeyBoardOpen()) {
                popup.showAtBottom();
                changeEmojiKeyboardIcon(imgSmiley, R.drawable.icon_keyboard);
            }

            //else, open the text keyboard first and immediately after that show the emoji popup
            else {
                edtMessage.setFocusableInTouchMode(true);
                edtMessage.requestFocus();
                popup.showAtBottomPending();
                final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(edtMessage, InputMethodManager.SHOW_IMPLICIT);
                changeEmojiKeyboardIcon(imgSmiley, R.drawable.icon_keyboard);
            }
        }

        //If popup is showing, simply dismiss it to show the undelying text keyboard
        else {
            popup.dismiss();
            changeEmojiKeyboardIcon(imgSmiley, R.drawable.icon_smiley);
        }
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
}
