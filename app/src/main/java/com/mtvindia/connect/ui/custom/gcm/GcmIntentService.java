package com.mtvindia.connect.ui.custom.gcm;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.PushMessage;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.ui.activity.LaunchActivity;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.util.UserPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class GcmIntentService extends IntentService {

    @Inject UserPreference userPreference;
    @Inject Gson gson;

    private static final int MY_NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;
    private PendingIntent contentIntent;
    private NotificationCompat.Builder mBuilder;

    private List<PushMessage> pushMessageList;

    private Set<Integer> conversation;
    private int size;

    public GcmIntentService() {
        super("GcmIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Injector.instance().inject(this);
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                receivedNotification(extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                receivedNotification(extras);

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                receivedNotification(extras);
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void receivedNotification(Bundle message) {
        String msg = null;
        try {
            msg = new JSONObject(message.get("custom").toString()).getString("a");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        PushMessage pushMessage = gson.fromJson(msg, PushMessage.class);
        int id = pushMessage.getId();
        //int id = Integer.parseInt(msg.getString("fromUserId").split("user")[1].split("@")[0]);
/*

        pushMessage.setId(id);
        pushMessage.setName(msg.getString("name"));
        pushMessage.setMessage(msg.getString("message"));
*/

        pushMessageList = userPreference.readPushMessage();
        pushMessageList.add(pushMessage);
        userPreference.savePushMessage(pushMessageList);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        pushMessageList = userPreference.readPushMessage();
        size = pushMessageList.size();

        conversation = getConversationList(pushMessageList);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        if (userPreference.readUser() == null) {
            Intent intent = new Intent(this, LaunchActivity.class);
            stackBuilder.addParentStack(LaunchActivity.class);
            stackBuilder.addNextIntent(intent);
            //contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, LaunchActivity.class), 0);
        } else if (conversation.size() > 1) {
            Intent intent = new Intent(this, NavigationActivity.class);
            stackBuilder.addParentStack(NavigationActivity.class);
            stackBuilder.addNextIntent(intent);
        } else {
            Intent intent = new Intent(this, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("userId", id);
            intent.putExtras(bundle);
            stackBuilder.addParentStack(ChatActivity.class);
            stackBuilder.addNextIntent(intent);
        }

        contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        if (size > 1) {
            String content = size + " messages";
            String title = (conversation.size() > 1) ? conversation.size() + " conversation" : pushMessage.getName();

            mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(getNotificationIcon())
                            .setContentTitle(title)
                            .setColor(getResources().getColor(R.color.purple))
                            .setContentText(content)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                            .setAutoCancel(true);
        } else {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(pushMessageList.get(0).getName())
                    .setColor(getResources().getColor(R.color.purple))
                    .setContentText(pushMessageList.get(0).getMessage())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(pushMessageList.get(0).getMessage()))
                    .setAutoCancel(true);
        }

        mBuilder.setContentIntent(contentIntent);

        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;
        mNotificationManager.notify(MY_NOTIFICATION_ID /*(int) (System.currentTimeMillis()/1000)*/, mBuilder.build());
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_stat_gamethrive_default : R.drawable.icon_launcher;
    }

    private Set<Integer> getConversationList(List<PushMessage> pushMessageList) {
        conversation = new HashSet<>();
        for (PushMessage pushMessage1 : pushMessageList) {
            conversation.add(pushMessage1.getId());
        }

        return conversation;
    }
/*
    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
        try {
            if (additionalData != null) {
                PushMessage pushMessage = new PushMessage();
                pushMessage.setMessage(additionalData.getString("message"));
                pushMessage.setName(additionalData.getString("name"));
                pushMessage.setId((Integer) additionalData.get("fromUserId"));

                Log.d("OneSignalExample", "Full additionalData:\n" + additionalData.toString());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }*/
}
