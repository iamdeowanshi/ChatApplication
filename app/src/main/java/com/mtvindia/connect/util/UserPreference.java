package com.mtvindia.connect.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.PushMessage;
import com.mtvindia.connect.data.model.User;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Sibi on 06/11/15.
 */
public class UserPreference {

    @Inject PreferenceUtil preferenceUtil;
    @Inject Gson gson;

    public static final String USER = "_USER" ;
    public static final String IS_IN_REGISTRATION = "_IS_REGISTERED";
    public static final String MATCHED_USER = "_MATCHED_USER";
    public static final String DEVICE_TOKEN = "_DEVICE_TOKEN";
    public static final String PUSH_NOTIFICATION = "_PUSH_NOTIFICATION";

    public UserPreference() {
        Injector.instance().inject(this);
    }

    public void saveUser(User user) {
        if (readUser() == null) {
            preferenceUtil.save(USER, user);
            return;
        }

        if (user.getAccessToken() == null || user.getAccessToken().equals("")) {
            user.setAccessToken(readUser().getAccessToken());
            user.setAccessToken(readUser().getAccessToken());
            preferenceUtil.save(USER, user);
        }
    }

    public User readUser() {
        return (User) preferenceUtil.read(USER, User.class);
    }

    public void saveLoginStatus(boolean status) {
        preferenceUtil.save(IS_IN_REGISTRATION, status);
    }

    public boolean readLoginStatus() {
        return preferenceUtil.readBoolean(IS_IN_REGISTRATION, false);
    }

    public void saveMatchedUser(List<User> users) {
        String usersJson = gson.toJson(users);

        preferenceUtil.save(MATCHED_USER, usersJson);
    }

    public List<User> readMatchedUser() {
        String usersJson = preferenceUtil.readString(MATCHED_USER, "[]");

        Type listType = new TypeToken<List<User>>() {}.getType();

        return gson.fromJson(usersJson, listType);
    }

    public void saveDeviceToken(String token) {
        preferenceUtil.save(DEVICE_TOKEN, token);
    }

    public String readDeviceToken() {
        return preferenceUtil.readString(DEVICE_TOKEN, "");
    }

    public void savePushMessage(PushMessage message) {
        String pushMessage = gson.toJson(message);

        preferenceUtil.save(PUSH_NOTIFICATION, pushMessage);
    }

    public List<PushMessage> readPushMessage() {
        String pushMessage = preferenceUtil.readString(PUSH_NOTIFICATION, "[]");

        Type listType = new TypeToken<List<PushMessage>>() {}.getType();

        return gson.fromJson(pushMessage, listType);
    }

    public void removeMatchedUser() {
        preferenceUtil.remove(MATCHED_USER);
    }

    public void removeUser() {
        preferenceUtil.remove(USER);
        preferenceUtil.remove(IS_IN_REGISTRATION);
    }

    public void removePushMessage() {
        preferenceUtil.remove(PUSH_NOTIFICATION);
    }

}
