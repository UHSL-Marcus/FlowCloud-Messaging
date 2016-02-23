package com.uhsl.flowmessage.flowmessagev2.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Marcus on 19/02/2016.
 */
public class ConfigSettings {

    public static final String SETTINGS = "SETTINGS";
    public static final String DEFAULT_VALUE = "EMPTY";

    public static final String SERVER = "SERVER";
    public static final String OAUTH_KEY = "OAUTH_KEY";
    public static final String OAUTH_SECRET = "OAUTH_SECRET";

    public static void clearAll(Context context) {
        context.getSharedPreferences(ConfigSettings.SETTINGS, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public static void saveServerSettings(Context context, String sever, String oAuthKey, String oAuthSecret) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConfigSettings.SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ConfigSettings.OAUTH_KEY, oAuthKey);
        editor.putString(ConfigSettings.OAUTH_SECRET, oAuthSecret);
        editor.putString(ConfigSettings.SERVER, sever);
        editor.commit();
    }

    public static boolean checkServerSettings(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConfigSettings.SETTINGS, Context.MODE_PRIVATE);
        if (!sharedPreferences.contains(ConfigSettings.OAUTH_KEY)) return false;
        if (!sharedPreferences.contains(ConfigSettings.OAUTH_SECRET)) return false;
        if (!sharedPreferences.contains(ConfigSettings.SERVER))
        {

        };

        return true;
    }

    public static String getStringSetting(Context context, String key) {
        return context.getSharedPreferences(ConfigSettings.SETTINGS, Context.MODE_PRIVATE)
                .getString(key, ConfigSettings.DEFAULT_VALUE);
    }



}
