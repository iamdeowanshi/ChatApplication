package com.mtvindia.connect.ui.activity;

import com.mtvindia.connect.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sibi on 13/10/15.
 */
public class NavigationItem {

    private int icon;
    private String text;
    private static List<NavigationItem> list;

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

    public static List<NavigationItem> getNavigationListItems() {
        list = new ArrayList<NavigationItem>();

        NavigationItem item = new NavigationItem("Find more people", R.drawable.icon_find);
        list.add(item);
        item = new NavigationItem("Profile", R.drawable.icon_profile);
        list.add(item);
        item = new NavigationItem("Preferences", R.drawable.icon_pref);
        list.add(item);
        item = new NavigationItem("Chat", R.drawable.icon_chat);
        list.add(item);
        item = new NavigationItem("About", R.drawable.icon_about);
        list.add(item);
        item = new NavigationItem("Logout", R.drawable.icon_logout);
        list.add(item);

        return list;
    }

}
