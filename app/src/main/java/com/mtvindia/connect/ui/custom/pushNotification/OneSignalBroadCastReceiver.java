package com.mtvindia.connect.ui.custom.pushNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.PushMessage;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.ui.activity.LaunchActivity;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.util.UserPreference;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Sibi on 28/12/15.
 */
public class OneSignalBroadCastReceiver extends BroadcastReceiver implements OneSignal.NotificationOpenedHandler {

    @Inject UserPreference userPreference;
    @Inject Gson gson;
    @Inject Context context;

    private static final int MY_NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;
    private PendingIntent contentIntent;
    private NotificationCompat.Builder mBuilder;

    private List<PushMessage> pushMessageList;

    private Set<Integer> conversation;
    private int size;

    @Override
    public void onReceive(Context context, Intent intent1) {
        Injector.instance().inject(this);
        Bundle dataBundle = intent1.getBundleExtra("data");

        boolean isActive = (boolean) dataBundle.get("isActive");
        if ( isActive ) {

            JSONObject jsonObject = null;
            String msg = null;
            PushMessage pushMessage = new PushMessage();
            try {
                msg = new JSONObject(dataBundle.get("custom").toString()).getString("a");
                jsonObject = new JSONObject(msg);
                pushMessage.setId(Integer.parseInt(jsonObject.getString("fromUserId").split("user")[1]));
                pushMessage.setName(jsonObject.getString("name"));
                pushMessage.setMessage(jsonObject.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int id = pushMessage.getId();

            pushMessageList = userPreference.readPushMessage();
            pushMessageList.add(pushMessage);
            userPreference.savePushMessage(pushMessageList);

            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            pushMessageList = userPreference.readPushMessage();
            size = pushMessageList.size();

            conversation = getConversationList(pushMessageList);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            if (userPreference.readUser() == null) {
                Intent intent = new Intent(context, LaunchActivity.class);
                stackBuilder.addParentStack(LaunchActivity.class);
                stackBuilder.addNextIntent(intent);
                //contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, LaunchActivity.class), 0);
            } else if (conversation.size() > 1) {
                Intent intent = new Intent(context, NavigationActivity.class);
                stackBuilder.addParentStack(NavigationActivity.class);
                stackBuilder.addNextIntent(intent);
            } else {
                Intent intent = new Intent(context, ChatActivity.class);
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
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(getNotificationIcon())
                                .setContentTitle(title)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setColor(context.getResources().getColor(R.color.purple))
                                .setContentText(content)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                                .setAutoCancel(true);
            } else {
                mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(getNotificationIcon())
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentTitle(pushMessageList.get(0).getName())
                        .setColor(context.getResources().getColor(R.color.purple))
                        .setContentText(pushMessageList.get(0).getMessage().split(":")[1].trim())
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

    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
        Timber.d(String.valueOf(isActive));
    }
}



