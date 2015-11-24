package com.mtvindia.connect.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mtvindia.connect.app.di.Injector;
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

    public UserPreference() {
        Injector.instance().inject(this);
    }

    public void saveUser(User user) {
        if (readUser() == null) {
            preferenceUtil.save(UserPreference.USER, user);
            return;
        }

        if (user.getAccessToken() == null || user.getAccessToken().equals("")) {
            user.setAccessToken(readUser().getAccessToken());
            user.setAccessToken(readUser().getAccessToken());
            preferenceUtil.save(UserPreference.USER, user);
        }
    }

    public User readUser() {
        return (User) preferenceUtil.read(UserPreference.USER, User.class);
    }

    public void saveLoginStatus(boolean status) {
        preferenceUtil.save(UserPreference.IS_IN_REGISTRATION, status);
    }

    public boolean readLoginStatus() {
        return preferenceUtil.readBoolean(UserPreference.IS_IN_REGISTRATION, false);
    }

    public void saveMatchedUser(List<User> users) {
        String usersJson = gson.toJson(users);

        preferenceUtil.save(UserPreference.MATCHED_USER, usersJson);
    }

    public List<User> readMatchedUser() {
        String usersJson = preferenceUtil.readString(MATCHED_USER, "[]");

        Type listType = new TypeToken<List<User>>() {}.getType();

        return gson.fromJson(usersJson, listType);
    }

    public void removeMatchedUser() {
        preferenceUtil.remove(MATCHED_USER);
    }

    public void removeUser() {
        preferenceUtil.remove(USER);
        preferenceUtil.remove(IS_IN_REGISTRATION);
    }

}
