package com.mtvindia.connect.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.AboutUser;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.presenter.AboutUserPresenter;
import com.mtvindia.connect.presenter.AboutUserViewInteractor;
import com.mtvindia.connect.presenter.ChatListPresenter;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.util.UserPreference;

import org.joda.time.DateTime;

import java.util.ArrayList;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Sibi on 09/12/15.
 */
public class XmppReciever extends BroadcastReceiver implements AboutUserViewInteractor {

    @Inject ChatMessageRepository chatMessageRepository;
    @Inject ChatListRepository chatListRepository;
    @Inject UserPreference userPreference;
    @Inject AboutUserPresenter aboutUserPresenter;
    @Inject ChatListPresenter chatListPresenter;

    private int userId;
    private String from;
    private DateTime time;
    private User user;
    private String message;
    private Context context;
    private ChatList chatList = new ChatList();
    private ChatMessage chatMessage = new ChatMessage();

    private static final int MY_NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;
    private PendingIntent contentIntent;
    private NotificationCompat.Builder mBuilder;


    @Override
    public void onReceive(Context context, Intent intent) {
        Injector.instance().inject(this);
        this.context = context;
        String action = intent.getAction();
        user = userPreference.readUser();
        aboutUserPresenter.setViewInteractor(this);

        if(user == null) return;

        switch (action) {
            case SmackService.NEW_MESSAGE:
                String from = intent.getStringExtra(SmackService.BUNDLE_FROM_JID);
                message = intent.getStringExtra(SmackService.BUNDLE_MESSAGE_BODY);
                saveToDB(from, message);
                showNotification();
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

    private void showNotification() {
        if (userId != userPreference.readSelectedUser()) {

            ChatList chatList = chatListRepository.getUser(userId, user.getId());
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(context, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);
            intent.putExtras(bundle);
            stackBuilder.addParentStack(ChatActivity.class);
            stackBuilder.addNextIntent(intent);
            contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(getNotificationIcon())
                            .setContentTitle(chatList.getName().split(" ")[0])
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setColor(context.getResources().getColor(R.color.purple))
                            .setContentText(message)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);

            Notification note = mBuilder.build();
            note.defaults |= Notification.DEFAULT_VIBRATE;
            note.defaults |= Notification.DEFAULT_SOUND;
            mNotificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_stat_gamethrive_default : R.drawable.icon_launcher;
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
        chatListPresenter.addUser(userPreference.readUser().getId(), chatList.getUserId(), userPreference.readUser().getAuthHeader());
        chatListRepository.updateTime(userId, user.getId(), time.toString());

    }
}
