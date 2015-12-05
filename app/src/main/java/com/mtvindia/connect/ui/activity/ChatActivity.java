package com.mtvindia.connect.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.ui.adapter.ChatMessageAdapter;
import com.mtvindia.connect.ui.custom.UbuntuEditText;
import com.mtvindia.connect.util.UserPreference;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Sibi on 27/11/15.
 */
public class ChatActivity extends BaseActivity {

    @Inject
    ChatMessageRepository chatMessageRepository;
    @Inject
    ChatListRepository chatListRepository;
    @Inject
    UserPreference userPreference;

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbarActionbar;
    @Bind(R.id.chat_messages)
    RecyclerView chatMessages;
    @Bind(R.id.icon_back)
    ImageButton iconBack;
    @Bind(R.id.img_dp)
    ImageView imgDp;
    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.icon_send)
    ImageView iconSend;
    @Bind(R.id.edt_message)
    UbuntuEditText edtMessage;

    private ChatList chatList;
    private List<ChatMessage> chatMessagesList;
    private ChatMessage message = new ChatMessage();
    private int userId;
    private User user;
    private ChatMessageAdapter chatMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        injectDependencies();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
       // layoutManager.setReverseLayout(true);

        userId = getIntent().getIntExtra("userId", 0);
        chatList = chatListRepository.find(userId);
        chatMessagesList = chatMessageRepository.searchMessage("webUser" + userId);

        chatMessages.setLayoutManager(layoutManager);
        chatMessages.setHasFixedSize(true);
        chatMessages.scrollToPosition(chatMessagesList.size() -1);


        user = userPreference.readUser();

        chatMessageAdapter = new ChatMessageAdapter(this, chatMessagesList);
        chatMessages.setAdapter(chatMessageAdapter);

        setSupportActionBar(toolbarActionbar);
        getSupportActionBar().setTitle(chatList.getName().toString());
        txtName.setText(chatList.getName());
        Picasso.with(this).load(chatList.getImage()).fit().into(imgDp);

        iconBack.setOnClickListener(new View.OnClickListener() {
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
    }

    private void sendMessage() {
        DateTime time = DateTime.now();
        message.setCreatedTime(time.toString());
        message.setBody(edtMessage.getText().toString());
        message.setStatus("delivered");
        message.setFrom("webUser" + user.getId());
        message.setTo("webUser" + userId);
        message.setUserId(chatList.getId());
        chatListRepository.updateTime(userId, time.toString());
        edtMessage.setText("");
        chatMessageRepository.save(message);
        chatMessageAdapter.notifyDataSetChanged();
        chatMessages.scrollToPosition(chatMessagesList.size() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return false;
    }

    @Override
    public void onBackPressed() {
        startActivity(NavigationActivity.class, null);
        finish();
    }
}
