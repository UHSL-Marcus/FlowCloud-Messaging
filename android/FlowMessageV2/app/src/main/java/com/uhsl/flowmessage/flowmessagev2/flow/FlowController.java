package com.uhsl.flowmessage.flowmessagev2.flow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.imgtec.flow.client.core.Core;
import com.imgtec.flow.client.core.NetworkException;
import com.uhsl.flowmessage.flowmessagev2.utils.ConfigSettings;

import java.net.MalformedURLException;

/**
 * Created by Marcus on 19/02/2016.
 */
public class FlowController {

    private static volatile FlowController instance;

    private static boolean flowInit = false;
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
        sharedPreferences = context.getSharedPreferences(ConfigSettings.SETTINGS, Context.MODE_PRIVATE);
    }

    public boolean initFlowIfNot(Activity activity) {
        boolean result = false;
        if (!flowInit) {
            result = flowInit(activity);
            if (result && Core.getDefaultClient().getAPI().hasSettings())
                flowInit = true;
        }

        return result;
    }

    public boolean reinitiliseFlow(Activity activity) {
        if (shutdownFlow()) {
            return initFlowIfNot(activity);
        }
        return false;
    }

    public boolean shutdownFlow() {
        if (flowConnection.getFlowInstance().shutdown())
            flowInit = false;

        return !flowInit;
    }


    public boolean flowInit(Activity activity)  {
        String server = ConfigSettings.getStringSetting(activity, ConfigSettings.SERVER);
        if (flowConnection.testServerUrl(server)) {
            return flowConnection.getFlowInstance().init(
                    flowConnection.getInitXML(
                            server,
                            ConfigSettings.getStringSetting(activity, ConfigSettings.OAUTH_KEY),
                            ConfigSettings.getStringSetting(activity, ConfigSettings.OAUTH_SECRET)
                    )
            );
        } else {
            return false;
        }

    }



    public boolean isFlowInit() {
        return flowInit;
    }

    public boolean isUserLoggedIn () {
        return Core.getDefaultClient().isUserLoggedIn();
    }





}
