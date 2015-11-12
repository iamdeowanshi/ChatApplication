package com.mtvindia.connect.util;

import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.User;

import javax.inject.Inject;

/**
 * Created by Sibi on 06/11/15.
 */
public class UserPreference {

    @Inject PreferenceUtil preferenceUtil;

    public static final String USER = "_USER" ;
    public static final String IS_IN_REGISTRATION = "_IS_REGISTERED";

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

    public void removeUser() {
        preferenceUtil.remove(USER);
        preferenceUtil.remove(IS_IN_REGISTRATION);
    }

}
