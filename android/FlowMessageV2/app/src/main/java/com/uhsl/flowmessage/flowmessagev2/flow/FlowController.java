package com.uhsl.flowmessage.flowmessagev2.flow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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
            result = flowInit();
            if (result)
                flowInit = true;
            //TODO: snackbar -> connection failed, check server settings
        } // TODO: flow already initalised
        return result;
    }

    public boolean flowInit()  {
        try {
            return flowConnection.getFlowInstance().init(
                    flowConnection.getInitXML(
                            sharedPreferences.getString(ConfigSettings.SERVER, ConfigSettings.DEFAULT_VALUE),
                            sharedPreferences.getString(ConfigSettings.OAUTH_KEY, ConfigSettings.DEFAULT_VALUE),
                            sharedPreferences.getString(ConfigSettings.OAUTH_SECRET, ConfigSettings.DEFAULT_VALUE)
                    )
            );
        } catch (NetworkException e) {
            System.out.println("Network Exception");
            return false;
        }
        catch (Exception e) {
            System.out.println("Exception");
            return false;
        }
    }

    public boolean isFlowInit() {
        return flowInit;
    }





}
