package com.mtvindia.connect.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class SmackService extends Service {

    public static final String NEW_MESSAGE = "com.mtvindia.connect.newmessage";
    public static final String SEND_MESSAGE = "com.mtvindia.connect.sendmessage";
    public static final String NEW_ROSTER = "com.mtvindia.connect.newroster";

    public static final String BUNDLE_FROM_JID = "b_from";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_ROSTER = "b_body";
    public static final String BUNDLE_TO = "b_to";

    public static SmackConnection.ConnectionState connectionState;

    public static SmackConnection.ConnectionState getState() {
        if (connectionState == null) {
            return SmackConnection.ConnectionState.DISCONNECTED;
        }
        return connectionState;
    }

    private boolean isActive;
    private Thread thread;
    private Handler threadHandler;
    private SmackConnection connection;

    public SmackService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    public void start() {
        if ( !isActive) {
            isActive = true;
            // Create ConnectionThread Loop
            if (thread == null || ! thread.isAlive()) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        threadHandler = new Handler();
                        initConnection();
                        Looper.loop();
                    }

                });
                thread.start();
            }

        }
    }

    public void stop() {
        isActive = false;
        if (threadHandler == null) return;

        threadHandler.post(new Runnable() {

            @Override
            public void run() {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void initConnection() {
        if (connection == null) {
            connection = new SmackConnection(this);
        }
        try {
            connection.connect();
        } catch (IOException | SmackException | XMPPException e) {
            e.printStackTrace();
        }
    }

}
