package com.mtvindia.connect.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.Config;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.data.model.AboutUser;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;
import com.mtvindia.connect.presenter.AboutUserPresenter;
import com.mtvindia.connect.presenter.AboutUserViewInteractor;
import com.mtvindia.connect.presenter.ChatListPresenter;
import com.mtvindia.connect.services.SmackService;
import com.mtvindia.connect.ui.adapter.ChatMessageAdapter;
import com.mtvindia.connect.util.UserPreference;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.List;

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
 * Created by Sibi on 27/11/15.
 */
public class ChatActivity extends BaseActivity implements AboutUserViewInteractor, DataChangeListener, PopupMenu.OnMenuItemClickListener {

    @Inject ChatMessageRepository chatMessageRepository;
    @Inject ChatListRepository chatListRepository;
    @Inject UserPreference userPreference;
    @Inject AboutUserPresenter presenter;
    @Inject ChatListPresenter chatListPresenter;

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbarActionbar;
    @Bind(R.id.chat_messages)
    RecyclerView chatMessages;
    @Bind(R.id.img_dp)
    ImageView imgDp;
    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.txt_status)
    TextView txtStatus;
    @Bind(R.id.edt_message)
    EmojiconEditText edtMessage;
    @Bind(R.id.img_smiley)
    ImageView imgSmiley;
    @Bind(R.id.root_view)
    LinearLayout rootView;

    private ChatList chatList;
    private List<ChatMessage> chatMessagesList;
    private int userId;
    private User user;
    private ChatMessageAdapter chatMessageAdapter;
    private String message;
    private EmojiconsPopup popup;
    private ProgressDialog progressDialog;
    private String userStatus;

    public static enum MessageState {
        Sending, Sent, Delivered, Read;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        injectDependencies();

        chatMessageRepository.reInitialize();
        chatListRepository.reInitialize();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        presenter.setViewInteractor(this);
        setEmojicons();

        chatMessages.setLayoutManager(layoutManager);
        chatMessages.setHasFixedSize(false);

        user = userPreference.readUser();
        userId = getIntent().getIntExtra("userId", 0);
        userPreference.saveSelectedUser(userId);

        loadUser();
        loadMessages();

        chatMessageAdapter = new ChatMessageAdapter(this, chatMessagesList);
        chatMessages.setAdapter(chatMessageAdapter);
    }

    private void setEmojicons() {
        popup = new EmojiconsPopup(rootView, this);
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
                chatMessageAdapter.notifyDataSetChanged();
                chatMessages.scrollToPosition(chatMessagesList.size() - 1);
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

    @OnClick(R.id.icon_send)
    void onClickSendIcon() {
        sendMessage();
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
                final InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
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

    @OnClick(R.id.back_layout)
    void onBackClick() {
        onBackPressed();
    }

    private void loadMessages() {
        chatMessagesList = chatMessageRepository.searchMessage("webuser" + userId, "webuser" + user.getId());
        chatMessageRepository.setDataChangeListener(ChatActivity.this);
        chatListRepository.setDataChangeListener(ChatActivity.this);
        chatMessages.scrollToPosition(chatMessagesList.size() - 1);
    }

    private void loadUser() {
        chatList = chatListRepository.find(userId, user.getId());

        if (chatList == null) {
            presenter.getAboutUser(userId, user.getAuthHeader());

        } else {
            setSupportActionBar(toolbarActionbar);
            getSupportActionBar().setTitle(chatList.getName().toString());
            txtName.setText(chatList.getName());
            Picasso.with(this).load(chatList.getImage()).fit().into(imgDp);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, SmackService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectChatServer();
    }

    private void disconnectChatServer() {
        Intent intent = new Intent(this, SmackService.class);
        this.stopService(intent);
    }

    private void sendMessage() {
        message = edtMessage.getText().toString().trim();

        if (message.equals("")) {
            edtMessage.setText("");
            return;
        }

        DateTime time = DateTime.now();
        edtMessage.setText("");

        updateTime();
        //chatListRepository.updateTime(userId, user.getId(), DateTime.now().toString());
        chatMessageAdapter.notifyDataSetChanged();
        chatMessages.scrollToPosition(chatMessagesList.size() - 1);

        sendToChatServer();
    }

    private void updateTime() {
        chatListRepository.updateTime(userId, user.getId(), DateTime.now().toString());

    }

    private void sendToChatServer() {
        Intent intent = new Intent(SmackService.SEND_MESSAGE);
        intent.setPackage(this.getPackageName());
        intent.putExtra(SmackService.BUNDLE_MESSAGE_BODY, message);
        intent.putExtra(SmackService.BUNDLE_TO, "webuser" + userId + "@" + Config.CHAT_SERVER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        }
        this.sendBroadcast(intent);
    }

    @Override
    public void onChange(List updatedData) {
        chatMessageAdapter.notifyDataSetChanged();
        chatMessages.scrollToPosition(chatMessagesList.size() - 1);
    }

    @Override
    public void onStatusChanged(String status) {
        txtStatus.setText(onlineStatus());
    }

    private String onlineStatus() {
        userStatus = chatListRepository.getStatus(userId, user.getId());

        return userStatus;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Fetching Details about User");
        progressDialog.show();
        Timber.d("progress started");
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
        Timber.d("progress removed");
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void aboutUser(AboutUser aboutUser) {
        chatList = new ChatList();
        DateTime time = DateTime.now();
        chatList.setUserId(aboutUser.getId());
        chatList.setImage(aboutUser.getProfilePic());
        chatList.setName(aboutUser.getFullName());
        chatList.setLastMessage("");
        chatList.setTime(time.toString());
        chatList.setLogedinUser(userPreference.readUser().getId());
        chatListRepository.save(chatList);
        chatListPresenter.addUser(userPreference.readUser().getId(), chatList.getUserId(), userPreference.readUser().getAuthHeader());

        setSupportActionBar(toolbarActionbar);
        getSupportActionBar().setTitle(chatList.getName().toString());
        txtName.setText(chatList.getName());
        Picasso.with(this).load(chatList.getImage()).fit().into(imgDp);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return false;
    }


    @Override
    public void onBackPressed() {
        userPreference.readSelectedUser();
        changeEmojiKeyboardIcon(imgSmiley, R.drawable.icon_smiley);
        startActivity(NavigationActivity.class, null);
        finish();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        userPreference.readSelectedUser();
    }
}
