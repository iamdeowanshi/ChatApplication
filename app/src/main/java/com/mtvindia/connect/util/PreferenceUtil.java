package com.mtvindia.connect.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mtvindia.connect.app.di.Injector;

import javax.inject.Inject;

public class PreferenceUtil {

    public static final String PREF_USER_KEY = "_USER" ;

    @Inject Context context;
    @Inject Gson gson;

    public PreferenceUtil() {
        Injector.instance().inject(this);
    }

    public void save(String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    } 

    public void save(String key, Object object) {
        save(key, gson.toJson(object));
    }

    public String read(String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try { 
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
             e.printStackTrace();
             return defaultValue;
        } 
    }

    public Object read(String key, Class classType) {
        return gson.fromJson(read(key, "{}"), classType);
    }

} 