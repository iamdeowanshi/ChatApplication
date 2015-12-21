package com.mtvindia.connect.ui.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.Config;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.data.repository.DataChangeListener;
import com.mtvindia.connect.services.SmackService;
import com.mtvindia.connect.ui.adapter.ChatMessageAdapter;
import com.mtvindia.connect.util.UserPreference;
import com.mtvindia.connect.util.ViewUtil;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sibi on 27/11/15.
 */
public class ChatActivity extends BaseActivity implements DataChangeListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, PopupMenu.OnMenuItemClickListener {

    @Inject
    ChatMessageRepository chatMessageRepository;
    @Inject
    ChatListRepository chatListRepository;
    @Inject
    UserPreference userPreference;
    @Inject
    ViewUtil viewUtil;

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
    @Bind(R.id.img_smiley)
    ImageButton smiley;
    @Bind(R.id.icon_send)
    ImageButton iconSend;
    @Bind(R.id.back_layout)
    LinearLayout backClick;
    @Bind(R.id.edt_message)
    EmojiconEditText edtMessage;
    @Bind(R.id.emojicons)
    FrameLayout emojicons;

    private ChatList chatList;
    private List<ChatMessage> chatMessagesList;
    private ChatMessage chatMessage = new ChatMessage();
    private int userId;
    private User user;
    private ChatMessageAdapter chatMessageAdapter;
    private String message;

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(edtMessage);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(edtMessage, emojicon);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    public static enum MessageState {
        Sending, Sent, Delivered, Read;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        injectDependencies();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // layoutManager.setReverseLayout(true);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, (int) (getHeight() * .4));
        emojicons.setLayoutParams(lp);

        user = userPreference.readUser();
        userId = getIntent().getIntExtra("userId", 0);
        chatList = chatListRepository.find(userId, user.getId());
        chatMessagesList = chatMessageRepository.searchMessage("webuser" + userId, "webuser" + user.getId());
        chatMessageRepository.setDataChangeListener(this);
        chatListRepository.setDataChangeListener(this);
        chatMessages.setLayoutManager(layoutManager);
        chatMessages.setHasFixedSize(false);
        chatMessages.scrollToPosition(chatMessagesList.size() - 1);


        chatMessageAdapter = new ChatMessageAdapter(this, chatMessagesList);
        chatMessages.setAdapter(chatMessageAdapter);

        setSupportActionBar(toolbarActionbar);
        getSupportActionBar().setTitle(chatList.getName().toString());
        txtName.setText(chatList.getName());
        Picasso.with(this).load(chatList.getImage()).fit().into(imgDp);

        backClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NavigationActivity.class, null);
                finish();
            }
        });

        iconSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

   /*     smiley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojicons.getVisibility() == View.GONE ) {
                    if(keyopen()) viewUtil.hide(getApplicationContext());
                    setEmojiconFragment(false);
                    emojicons.setVisibility(View.VISIBLE);
                } else {
                    emojicons.setVisibility(View.GONE);
                }
            }
        });
        edtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojicons.getVisibility() == View.VISIBLE) emojicons.setVisibility(View.GONE);
            }
        });*/

        edtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chatMessages.scrollToPosition(chatMessagesList.size() - 1);
                    }
                }, 1000);
            }
        });

    }

    public boolean keyopen() {
        Rect rectgle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        int curheight = rectgle.bottom;

        if (curheight != getHeight()) {
            return true;
        } else {
            return false;
        }
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    private int getHeight() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        return metrics.heightPixels;
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
        chatMessage.setCreatedTime(time.toString());
        chatMessage.setBody(message);
        chatMessage.setStatus(MessageState.Sending.toString());
        chatMessage.setFrom("webuser" + user.getId());
        chatMessage.setTo("webuser" + userId);
        chatMessage.setUserId(userId);
        chatListRepository.updateTime(userId, user.getId(), time.toString());
        edtMessage.setText("");
        chatMessageRepository.save(chatMessage);
        chatMessageAdapter.notifyDataSetChanged();
        chatMessages.scrollToPosition(chatMessagesList.size() - 1);
        sendTOChatServer();
    }

    private void sendTOChatServer() {
        Intent intent = new Intent(SmackService.SEND_MESSAGE);
        intent.setPackage(this.getPackageName());
        intent.putExtra(SmackService.BUNDLE_MESSAGE_BODY, message);
        intent.putExtra(SmackService.BUNDLE_TO, chatMessage.getTo() + "@" + Config.CHAT_SERVER);
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
        txtStatus.setText(chatListRepository.getStatus(userId, user.getId()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (emojicons.getVisibility() == View.VISIBLE) {
            emojicons.setVisibility(View.GONE);
            return;
        }
        startActivity(NavigationActivity.class, null);
        finish();
    }
}
