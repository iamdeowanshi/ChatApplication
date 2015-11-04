package com.mtvindia.connect.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mtvindia.connect.app.di.Injector;

import java.util.Set;

import javax.inject.Inject;

public class PreferenceUtil {

    public static final String USER = "_USER" ;
    public static final String QUESTIONS_ANSWERED = "_NO_OF_QUESTIONS_ANSWERED";
    public static final String IS_IN_REGISTRATION = "_IS_REGISTERED";
    public static final String PRIMARY_QUESTION_ID = "_PRIMARY_QUESTION_ID";
    public static final String RESULT_RESPONSE = "_RESULT_RESPONSE";

    @Inject Context context;
    @Inject Gson gson;

    private SharedPreferences preferences;

    public PreferenceUtil() {
        Injector.instance().inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Save methods

    public void save(String key, Object object) {
        save(key, gson.toJson(object));
    }

    public void save(String key, int value) {
        getEditor().putInt(key, value).apply();
    }

    public void save(String key, boolean value) {
        getEditor().putBoolean(key, value).apply();
    }

    public void save(String key, long value) {
        getEditor().putLong(key, value).apply();
    }

    public void save(String key, float value) {
        getEditor().putFloat(key, value).apply();
    }

    public void save(String key, Set<String> values) {
        getEditor().putStringSet(key, values).apply();
    }

    public void save(String key, String value) {
        getEditor().putString(key, value).apply();
    }

    // Read methods

    public Object read(String key, Class classType) {
        return gson.fromJson(readString(key, null), classType);
    }

    public int readInt(String key, int defaultValue) {
        try {
            return preferences.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public boolean readBoolean(String key, boolean defaultValue) {
        try {
            return preferences.getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public long readLong(String key, long defaultValue) {
        try {
            return preferences.getLong(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public float readFloat(String key, float defaultValue) {
        try {
            return preferences.getFloat(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public String readString(String key, String defaultValue) {
        try {
            return preferences.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

} 