package com.uhsl.flowmessage.flowmessagev2.flow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.imgtec.flow.ErrorType;
import com.imgtec.flow.client.core.Core;
import com.imgtec.flow.client.core.NetworkException;
import com.imgtec.flow.client.users.Device;
import com.imgtec.flow.client.users.User;
import com.imgtec.flow.client.users.UserHelper;
import com.uhsl.flowmessage.flowmessagev2.utils.ConfigSettings;

import java.net.MalformedURLException;
import java.util.List;

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
        if (shutdownFlow(false)) {
            return initFlowIfNot(activity);
        }
        return false;
    }

    public boolean shutdownFlow(boolean clearSession) {

        if (logoutUser(clearSession) && flowConnection.getFlowInstance().shutdown())
            flowInit = false;

        return !flowInit;
    }

    public boolean logoutUser(boolean clearSession) {
        if (clearSession)
            flowConnection.clearUserCredentials();

        return flowConnection.getFlowInstance().logOut(flowConnection.getUserFlowHandler());
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

    public boolean hasSavedCredentials() {
        return flowConnection.getUsername() != null && flowConnection.getPassword() != null;
    }

    public boolean userLogin(String username, String password) {
        if (!isUserLoggedIn()) {
            User user = UserHelper.newUser(Core.getDefaultClient());
            boolean loggedIn = flowConnection.getFlowInstance().userLogin(username, password, user, flowConnection.getUserFlowHandler());
            if (loggedIn) {
                flowConnection.setUserCredentials(username, password);
                flowConnection.subscribeAsyncMessages();
            }

            return loggedIn;
        }
        return true;
    }

    public boolean userReLogin() {
        System.out.println("saved detils: " + flowConnection.getUsername() + " : " + flowConnection.getPassword());
        return userLogin(flowConnection.getUsername(), flowConnection.getPassword());
    }

    public ErrorType getLastFlowError() {
        return flowConnection.getFlowInstance().getLastError();
    }

    public boolean isFlowInit() {
        return flowInit;
    }

    public boolean isUserLoggedIn () {
        return Core.getDefaultClient().isUserLoggedIn();
    }

    public List<Device> requestDevices(boolean refreshed) {
        if (refreshed)
            return flowConnection.getUserDevices();

        return flowConnection.getDeviceCache();
    }

    public void setConnectedDevice(Device device) {
        flowConnection.setCurrentDevice(device);
    }

    public Device getConnectedDevice() {
        return flowConnection.getCurrentDevice();
    }

    public void setAsyncMessageListener(AsyncMessageListener asyncMessageListener) {
        flowConnection.setAsyncMessageListener(asyncMessageListener);
    }

    public boolean sendAsyncMessage(String recipient, String message) {
        return flowConnection.getFlowInstance().sendAsyncMessage(flowConnection.getUserFlowHandler(),
                new String[]{recipient}, message);
    }

    public String getUserMessageingID() {
        return flowConnection.getUserAOR();
    }

    public String getUserID() {
        return flowConnection.getUserID();
    }

}
