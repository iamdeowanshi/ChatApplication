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
 * @author Aaditya Deowanshi
 *         Utility class that which saves user related data to shared preferences and make use of Preference util class.
 */

public class UserPreference {

    @Inject PreferenceUtil preferenceUtil;
    @Inject Gson gson;

    public static final String USER = "_USER";
    public static final String IS_IN_REGISTRATION = "_IS_REGISTERED";
    public static final String MATCHED_USER = "_MATCHED_USER";
    public static final String DEVICE_TOKEN = "_DEVICE_TOKEN";
    public static final String PUSH_NOTIFICATION = "_PUSH_NOTIFICATION";
    public static final String SELECTED_USER = "_SELECTED_USER";

    public UserPreference() {
        Injector.instance().inject(this);
    }

    /**
     * Saves user object.
     *
     * @param user
     */
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

    /**
     * Returns User object.
     *
     * @return
     */
    public User readUser() {
        return (User) preferenceUtil.read(USER, User.class);
    }

    /**
     * Saves status whether it is login or registration.
     *
     * @param status
     */
    public void saveLoginStatus(boolean status) {
        preferenceUtil.save(IS_IN_REGISTRATION, status);
    }

    /**
     * Returns status whether it is login or registration.
     *
     * @return
     */
    public boolean readLoginStatus() {
        return preferenceUtil.readBoolean(IS_IN_REGISTRATION, false);
    }

    /**
     * Save matched user to shared preference temporarily.
     *
     * @param users
     */
    public void saveMatchedUser(List<User> users) {
        String usersJson = gson.toJson(users);

        preferenceUtil.save(MATCHED_USER, usersJson);
    }

    /**
     * Returns the list of matched user.
     *
     * @return
     */
    public List<User> readMatchedUser() {
        String usersJson = preferenceUtil.readString(MATCHED_USER, "[]");

        Type listType = new TypeToken<List<User>>() {
        }.getType();

        return gson.fromJson(usersJson, listType);
    }

    /**
     * Saves device Token.
     *
     * @param token
     */
    public void saveDeviceToken(String token) {
        preferenceUtil.save(DEVICE_TOKEN, token);
    }

    /**
     * Returns device token.
     *
     * @return
     */
    public String readDeviceToken() {
        return preferenceUtil.readString(DEVICE_TOKEN, "");
    }

    /**
     * Saves push messages to shared preferences.
     *
     * @param message
     */
    public void savePushMessage(List<PushMessage> message) {
        String pushMessage = gson.toJson(message);

        preferenceUtil.save(PUSH_NOTIFICATION, pushMessage);
    }

    /**
     * Returns push messages from shared preferences.
     *
     * @return
     */
    public List<PushMessage> readPushMessage() {
        String pushMessage = preferenceUtil.readString(PUSH_NOTIFICATION, "[]");

        Type listType = new TypeToken<List<PushMessage>>() {
        }.getType();

        return gson.fromJson(pushMessage, listType);
    }

    /**
     * Saves id of selected user from chat list.
     *
     * @param id
     */
    public void saveSelectedUser(int id) {
        preferenceUtil.save(SELECTED_USER, id);
    }

    /**
     * Returns id of selected user with whom user is currently chatting.
     *
     * @return
     */
    public int readSelectedUser() {
        return preferenceUtil.readInt(SELECTED_USER, 0);
    }

    /**
     * Removes selected user from shared preference.
     */
    public void removeSelectedUser() {
        preferenceUtil.remove(SELECTED_USER);
    }

    /**
     * Clears shared preferences.
     */
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
