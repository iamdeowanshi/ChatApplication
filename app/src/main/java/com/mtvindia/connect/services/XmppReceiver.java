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
 * @author Aaditya Deowanshi
 *
 *         Xmpp receiver class to send and receive messages.
 */

public class XmppReceiver extends BroadcastReceiver implements AboutUserViewInteractor {

    @Inject ChatMessageRepository chatMessageRepository;
    @Inject ChatListRepository chatListRepository;
    @Inject UserPreference userPreference;
    @Inject AboutUserPresenter aboutUserPresenter;
    @Inject ChatListPresenter chatListPresenter;

    private static final int MY_NOTIFICATION_ID = 1;

    private int userId;
    private String from;
    private DateTime time;
    private User user;
    private String message;
    private Context context;
    private ChatList chatList = new ChatList();
    private ChatMessage chatMessage = new ChatMessage();

    private PendingIntent contentIntent;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Injector.instance().inject(this);
        this.context = context;
        String action = intent.getAction();
        user = userPreference.readUser();
        aboutUserPresenter.setViewInteractor(this);

        if (user == null) return;

        switch (action) {
            case SmackService.NEW_MESSAGE:
                String from = intent.getStringExtra(SmackService.BUNDLE_FROM_JID);
                message = intent.getStringExtra(SmackService.BUNDLE_MESSAGE_BODY);
                saveToDB(from, message);
                showNotification();
                break;
            case SmackService.NEW_ROSTER:
                ArrayList<String> roster = intent.getStringArrayListExtra(SmackService.BUNDLE_ROSTER);
                if (roster == null) return;

                for (String s : roster) {
                    Timber.d(s);
                    try {
                        int id = Integer.parseInt(s.split("user")[1].split("@")[0]);
                        String status = s.split(":")[1];
                        chatListRepository.reInitialize();
                        chatListRepository.updateStatus(id, user.getId(), status);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    // About user view interactor methods.
    @Override
    public void showProgress() {}

    @Override
    public void hideProgress() {}

    @Override
    public void onError(Throwable throwable) {}

    @Override
    public void aboutUser(AboutUser aboutUser) {
        chatList.setUserId(aboutUser.getId());
        chatList.setImage(aboutUser.getProfilePic());
        chatList.setName(aboutUser.getFullName());
        chatList.setLogedinUser(user.getId());
        chatListRepository.reInitialize();
        chatListRepository.save(chatList);
        chatListPresenter.addUser(userPreference.readUser().getId(), chatList.getUserId(), userPreference.readUser().getAuthHeader());
        chatListRepository.updateTime(userId, userPreference.readUser().getId(), DateTime.now().toString());
        createNotification();
    }

    /**
     * Method to show notification.
     */
    private void showNotification() {
        if (userId == userPreference.readSelectedUser()) return;

        chatList = chatListRepository.getUser(userId, user.getId());

        if (chatList != null) {
            createNotification();

            return;
        }

        aboutUserPresenter.getAboutUser(userId, user.getAuthHeader());
    }

    /**
     * Method to create notification.
     */
    private void createNotification() {
        String title = chatList.getName().split(" ")[0];
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
                        .setContentTitle(title)
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

    /**
     * Returns notification icon depending upon android version.
     * @return
     */
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);

        return useWhiteIcon ? R.drawable.ic_stat_gamethrive_default : R.drawable.icon_launcher;
    }

    /**
     * Saves messages to realm database.
     * @param from
     * @param message
     */
    private void saveToDB(String from, String message) {
        time = DateTime.now();
        this.from = from.split("@")[0];
        userId = Integer.parseInt(this.from.split("r")[1]);

        chatMessage.setCreatedTime(time.toString());
        chatMessage.setBody(message);
        chatMessage.setTo("webuser" + user.getId());
        chatMessage.setFrom(from.split("@")[0]);
        chatMessage.setUserId(userId);
        chatMessageRepository.reInitialize();
        chatMessageRepository.save(chatMessage);

        if (userPresent(userId)) {
            chatListRepository.reInitialize();
            chatListRepository.updateTime(userId, userPreference.readUser().getId(), DateTime.now().toString());
        }
    }

    /**
     * Checking whether user is present in database.
     * @param userId
     * @return
     */
    private boolean userPresent(int userId) {
        if (!chatListRepository.searchUser(userId, user.getId())) {
            aboutUserPresenter.getAboutUser(userId, user.getAuthHeader());

            return false;
        }

        return true;
    }

}
