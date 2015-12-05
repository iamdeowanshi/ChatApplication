package com.mtvindia.connect.data.model;

import com.mtvindia.connect.R;

/**
 * Created by Sibi on 13/10/15.
 */
public enum NavigationItem {

    FIND_PEOPLE("Find more people", R.drawable.icon_find),
    PROFILE("Profile", R.drawable.icon_profile),
    PREFERENCE("Preferences", R.drawable.icon_pref),
    CHAT("Chat", R.drawable.icon_chat),
    ABOUT("About",  R.drawable.icon_about),
    LOGOUT("Logout", R.drawable.icon_logout);

    private int icon;
    private String text;

    public int getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    NavigationItem(String name, int icon) {
        this.icon = icon;
        text = name;
    }

    @Override
    public String toString() {
        return text;
    }

}
