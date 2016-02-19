package com.uhsl.flowmessage.flowmessagev2.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Marcus on 19/02/2016.
 */
public class configSettings {

    public static final String SETTINGS = "SETTINGS";
    public static final String DEFAULT_VALUE = "EMPTY";

    public static final String SERVER = "SERVER";
    public static final String OAUTH_KEY = "OAUTH_KEY";
    public static final String OAUTH_SECRET = "OAUTH_SECRET";

    public static void saveServerSettings(Context context, String sever, String oAuthKey, String oAuthSecret) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(configSettings.SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(configSettings.OAUTH_KEY, oAuthKey);
        editor.putString(configSettings.OAUTH_SECRET, oAuthSecret);
        editor.putString(configSettings.SERVER, sever);
        editor.commit();
    }
}
