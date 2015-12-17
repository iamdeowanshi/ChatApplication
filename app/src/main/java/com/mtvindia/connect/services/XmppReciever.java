package com.mtvindia.connect.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.AboutUser;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.presenter.AboutUserPresenter;
import com.mtvindia.connect.presenter.AboutUserViewInteractor;
import com.mtvindia.connect.util.UserPreference;

import org.joda.time.DateTime;

import java.util.ArrayList;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Sibi on 09/12/15.
 */
public class XmppReciever extends BroadcastReceiver implements AboutUserViewInteractor {

    @Inject
    ChatMessageRepository chatMessageRepository;
    @Inject
    ChatListRepository chatListRepository;
    @Inject
    UserPreference userPreference;
    @Inject
    AboutUserPresenter aboutUserPresenter;

    private int userId;
    private String from;
    DateTime time;
    private ChatList chatList = new ChatList();
    private ChatMessage chatMessage = new ChatMessage();
    private User user;

    @Override
    public void onReceive(Context context, Intent intent) {
        Injector.instance().inject(this);
        String action = intent.getAction();
        user = userPreference.readUser();
        aboutUserPresenter.setViewInteractor(this);

        switch (action) {
            case SmackService.NEW_MESSAGE:
                String from = intent.getStringExtra(SmackService.BUNDLE_FROM_JID);
                String message = intent.getStringExtra(SmackService.BUNDLE_MESSAGE_BODY);
                saveToDB(from, message);
                break;
            case SmackService.NEW_ROSTER:
                ArrayList<String> roster = intent.getStringArrayListExtra(SmackService.BUNDLE_ROSTER);
                if (roster == null) {
                    return;
                }
                for (String s : roster) {
                    Timber.d(s);
                    int id = Integer.parseInt(s.split("user")[1].split("@")[0]);
                    String status = s.split(":")[1];
                    chatListRepository.updateStatus(id, user.getId(), status);
                }
                break;
        }

    }

    private void saveToDB(String from, String message) {
        time = DateTime.now();
        this.from = from.split("@")[0];
        userId = Integer.parseInt(this.from.split("r")[1]);

        chatMessage.setCreatedTime(time.toString());
        chatMessage.setBody(message);
        chatMessage.setTo("webuser" + user.getId());
        chatMessage.setFrom(from.split("@")[0]);
        chatMessage.setUserId(userId);
        chatMessageRepository.save(chatMessage);
        if(userPresent(userId)) {
            chatListRepository.updateTime(userId, user.getId(),  time.toString());
        }

    }

    private boolean userPresent(int userId) {
        if ( ! chatListRepository.searchUser(userId, user.getId())) {
            aboutUserPresenter.getAboutUser(userId, user.getAuthHeader());
            return false;
        }

        return true;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void aboutUser(AboutUser aboutUser) {
        chatList.setUserId(aboutUser.getId());
        chatList.setImage(aboutUser.getProfilePic());
        chatList.setName(aboutUser.getFullName());
        chatList.setLogedinUser(user.getId());
        chatListRepository.save(chatList);
        chatListRepository.updateTime(userId, user.getId(), time.toString());
    }
}
