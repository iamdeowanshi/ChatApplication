package com.mtvindia.connect.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.mtvindia.connect.app.Config;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.util.UserPreference;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ChatMessageListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import timber.log.Timber;

public class SmackConnection implements ConnectionListener, ChatManagerListener,
        RosterListener, ChatMessageListener, PingFailedListener {

    @Inject UserPreference userPreference;

    private Roster roster;

    public static enum ConnectionState {
        CONNECTED, CONNECTING, RECONNECTING, DISCONNECTED;
    }

    private static final String TAG = "SMACK";
    private final Context context;
    private final String password;
    private final String username;
    private final String serviceName;
    private User user;

    private XMPPTCPConnection connection;
    private ArrayList<String> userList;
    private BroadcastReceiver receiver;

    public SmackConnection(Context pContext) {
        Injector.instance().inject(this);
        user = userPreference.readUser();
        Timber.d("Chat Connection");
        context = pContext.getApplicationContext();
        serviceName = Config.CHAT_SERVER;
        username = "webuser" + user.getId();
        password = new StringBuilder(username).reverse().toString();
    }

    public void connect() throws IOException, XMPPException, SmackException {
        Log.i(TAG, "connect()");

        XMPPTCPConnectionConfiguration.XMPPTCPConnectionConfigurationBuilder builder = XMPPTCPConnectionConfiguration.builder();

        builder.setServiceName(serviceName);
        builder.setResource("SmackAndroidTestClient");
        builder.setUsernameAndPassword(username , password);
        builder.setRosterLoadedAtLogin(true);
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        connection = new XMPPTCPConnection(builder.build());

        //Set ConnectionListener here to catch initial connect();
        connection.addConnectionListener(this);

        connection.connect();
        connection.login();

        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

        roster = connection.getRoster();
        Timber.d(roster.toString());

        PingManager.setDefaultPingInterval(600); //Ping every 10 minutes
        PingManager pingManager = PingManager.getInstanceFor(connection);
        pingManager.registerPingFailedListener(this);

        setupSendMessageReceiver();

        ChatManager.getInstanceFor(connection).addChatListener(this);
        connection.getRoster().addRosterListener(this);
    }

    public void disconnect() {
        Log.i(TAG, "disconnect()");
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        try {
            if (connection != null) {
                connection.disconnect();
            }
        } catch (SmackException.NotConnectedException e) {
            SmackService.connectionState = ConnectionState.DISCONNECTED;
            e.printStackTrace();
        }

        connection = null;
    }

    private void rebuildRoster() {
        userList = new ArrayList<>();
        if(connection == null) return;
        String status;
        for (RosterEntry entry : connection.getRoster().getEntries()) {
            if (connection.getRoster().getPresence(entry.getUser()).isAvailable()) {
                status = "Online";
            } else {
                status = "Offline";
            }
            userList.add(entry.getUser() + ": " + status);
        }

        Intent intent = new Intent(SmackService.NEW_ROSTER);
        intent.setPackage(context.getPackageName());
        intent.putStringArrayListExtra(SmackService.BUNDLE_ROSTER, userList);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        }
        context.sendBroadcast(intent);
    }

    private void setupSendMessageReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(SmackService.SEND_MESSAGE)) {
                    sendMessage(intent.getStringExtra(SmackService.BUNDLE_MESSAGE_BODY), intent.getStringExtra(SmackService.BUNDLE_TO));
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(SmackService.SEND_MESSAGE);
        context.registerReceiver(receiver, filter);
    }

    private void sendMessage(String body, String toJid) {
        Log.i(TAG, "sendMessage()");
        Chat chat = ChatManager.getInstanceFor(connection).createChat(toJid, this);
        try {
            chat.sendMessage(body);
            roster.createEntry(toJid.split("/")[0],toJid.split("/")[0],null);
        } catch (SmackException.NotConnectedException | XMPPException e) {
            e.printStackTrace();
            toast("send failed");
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }

    //ChatListener
    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        Log.i(TAG, "chatCreated()");
        chat.addMessageListener(this);
    }

    //MessageListener
    @Override
    public void processMessage(Chat chat, Message message) {
        Log.i(TAG, "processMessage()");
        if (message.getType().equals(Message.Type.chat) || message.getType().equals(Message.Type.normal)) {
            if (message.getBody() != null) {
                Intent intent = new Intent(SmackService.NEW_MESSAGE);
                intent.setPackage(context.getPackageName());
                intent.putExtra(SmackService.BUNDLE_MESSAGE_BODY, message.getBody());
                intent.putExtra(SmackService.BUNDLE_FROM_JID, message.getFrom());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                }
                try {
                    roster.createEntry(message.getFrom().split("/")[0], message.getFrom().split("/")[0],null);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                context.sendBroadcast(intent);
                Log.i(TAG, "processMessage() BroadCast send");
            }
        }
    }

    //ConnectionListener
    @Override
    public void connected(XMPPConnection connection) {
        SmackService.connectionState = ConnectionState.CONNECTED;
        Timber.d("Connected");
    }

    @Override
    public void authenticated(XMPPConnection connection) {
        SmackService.connectionState = ConnectionState.CONNECTED;
        Timber.d("Authenticated");
    }

    @Override
    public void connectionClosed() {
        SmackService.connectionState = ConnectionState.DISCONNECTED;
        Timber.d("Connection Closed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        SmackService.connectionState = ConnectionState.DISCONNECTED;
        Timber.d("Connection Closed On Error");
        e.printStackTrace();
    }

    @Override
    public void reconnectingIn(int seconds) {
        SmackService.connectionState = ConnectionState.RECONNECTING;
        Log.i(TAG, "reconnectingIn()");
    }

    @Override
    public void reconnectionSuccessful() {
        SmackService.connectionState = ConnectionState.CONNECTED;
        Log.i(TAG, "reconnectionSuccessful()");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        SmackService.connectionState = ConnectionState.DISCONNECTED;
        Log.i(TAG, "reconnectionFailed()");
    }

    //RosterListener
    @Override
    public void entriesAdded(Collection<String> addresses) {
        Log.i(TAG, "entriesAdded()");
        rebuildRoster();
    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {
        Log.i(TAG, "entriesUpdated()");
        rebuildRoster();
    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {
        Log.i(TAG, "entriesDeleted()");
        rebuildRoster();
    }

    @Override
    public void presenceChanged(Presence presence) {
        Log.i(TAG, "presenceChanged()");
        rebuildRoster();
    }

    //PingFailedListener
    @Override
    public void pingFailed() {
        Log.i(TAG, "pingFailed()");
    }

    void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
