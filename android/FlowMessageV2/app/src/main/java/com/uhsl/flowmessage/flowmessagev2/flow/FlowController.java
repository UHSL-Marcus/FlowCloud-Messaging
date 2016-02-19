package com.uhsl.flowmessage.flowmessagev2.flow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.uhsl.flowmessage.flowmessagev2.utils.configSettings;

/**
 * Created by Marcus on 19/02/2016.
 */
public class FlowController {

    private static volatile FlowController instance;

    private static boolean isFlowInit = false;
    private FlowConnection flowConnection;
    private SharedPreferences sharedPreferences;
    private Context context;

    public static FlowController getInstance(Activity activity) {
        if (instance == null) {
            synchronized (FlowController.class) {
                if (instance == null) {
                    instance = new FlowController(activity);
                }
            }
        }
        return instance;
    }

    private FlowController(Activity activity) {
        flowConnection = FlowConnection.getInstance(activity);
        context = activity.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(configSettings.SETTINGS, Context.MODE_PRIVATE);
    }

    public boolean initFlowIfNot(Activity activity) {
        boolean result = false;
        if (!isFlowInit) {
            if (checkServerSettings()) {
                result = flowInit();
                if (result)
                    isFlowInit = true;
            } // TODO: snackbar prompting server settings
        } // TODO: flow already initalised
        return result;
    }

    public boolean flowInit() {
        return flowConnection.getFlowInstance().init(
                flowConnection.getInitXML(
                        sharedPreferences.getString(configSettings.SERVER, configSettings.DEFAULT_VALUE),
                        sharedPreferences.getString(configSettings.OAUTH_KEY, configSettings.DEFAULT_VALUE),
                        sharedPreferences.getString(configSettings.OAUTH_SECRET, configSettings.DEFAULT_VALUE)
                )
        );
    }

    public boolean checkServerSettings() {
        if (sharedPreferences.getString(configSettings.SERVER, configSettings.DEFAULT_VALUE).equals(configSettings.DEFAULT_VALUE))
            return false;

        if (sharedPreferences.getString(configSettings.OAUTH_SECRET, configSettings.DEFAULT_VALUE).equals(configSettings.DEFAULT_VALUE))
            return false;

        if (sharedPreferences.getString(configSettings.OAUTH_KEY, configSettings.DEFAULT_VALUE).equals(configSettings.DEFAULT_VALUE))
            return false;

        return true;
    }


}
