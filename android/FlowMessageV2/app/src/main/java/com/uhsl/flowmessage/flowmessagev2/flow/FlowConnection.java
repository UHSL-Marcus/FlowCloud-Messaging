package com.uhsl.flowmessage.flowmessagev2.flow;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.imgtec.flow.Flow;
import com.imgtec.flow.FlowHandler;
import com.imgtec.flow.MessagingEvent;
import com.imgtec.flow.client.core.Core;
import com.imgtec.flow.client.users.Device;
import com.imgtec.flow.client.users.Devices;
import com.imgtec.flow.client.users.User;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marcus on 19/02/2016.
 */
public class FlowConnection {

    private static volatile FlowConnection instance;

    private Flow flowInstance;
    private FlowHandler userFlowHandler;
    private String username = "mail+flow@marcuslee1.co.uk";
    private String password = "Sm@rtlab1234";
    private List<Device> deviceCache = new CopyOnWriteArrayList<>();
    private Device currentDevice;

    private Handler asyncMessageHandler;
    private Handler asyncResponseHandler;
    private AsyncMessageListener asyncMessageListener;


    private FlowConnection(Context context) {
        flowInstance = Flow.getInstance();
        flowInstance.setAppContext(context);
        userFlowHandler = new FlowHandler();
        initAsyncMessageHandlers();
    }

    public static FlowConnection getInstance(Context contex) {
        if (instance == null) {
            synchronized (FlowConnection.class) {
                if (instance == null) {
                    instance = new FlowConnection(contex);
                }
            }
        }
        return instance;
    }

    private void initAsyncMessageHandlers() {
        asyncMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                MessagingEvent messagingEvent = (MessagingEvent)msg.obj;
                asyncMessageListener.onMessageRecieved(messagingEvent);
            }
        };

        asyncResponseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                MessagingEvent messagingEvent = (MessagingEvent)msg.obj;
                asyncMessageListener.onResponseRecieved(messagingEvent);
            }
        };
    }

    public void subscribeAsyncMessages() {
        flowInstance.subscribe(getUserFlowHandler(), getUserAOR(),
                MessagingEvent.MessagingEventCategory.FLOW_MESSAGING_EVENTCATEGORY_ASYNC_MESSAGE, "", 1200, asyncMessageHandler);

        flowInstance.subscribe(getUserFlowHandler(), getUserAOR(),
                MessagingEvent.MessagingEventCategory.FLOW_MESSAGING_EVENTCATEGORY_ASYNC_MESSAGE_RESPONSE, "", 1200, asyncResponseHandler);
    }

    public String getUserAOR() {
        User user = Core.getDefaultClient().getLoggedInUser();
        return user.getFlowMessagingAddress().getAddress();
    }

    public Flow getFlowInstance(){
        return flowInstance;
    }

    public FlowHandler getUserFlowHandler() {
        return userFlowHandler;
    }

    public void setUserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void clearUserCredentials() {
        username = null;
        password = null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getInitXML(String server, String oAuthKey, String oAuthSecret) {
        return "<?xml version=\"1.0\"?>" +
                "<Settings>" +
                "<Setting>" +
                "<Name>restApiRoot</Name>" +
                "<Value>" + server + "</Value>" +
                "</Setting>" +
                "<Setting>" +
                "<Name>licenseeKey</Name>" +
                "<Value>" + oAuthKey + "</Value>" +
                "</Setting>" +
                "<Setting>" +
                "<Name>licenseeSecret</Name>" +
                "<Value>" + oAuthSecret + "</Value>" +
                "</Setting>" +
                "<Setting>" +
                "<Name>configDirectory</Name>" +
                "<Value>/mnt/img_messagingtest/outlinux/bin/config</Value>" +
                "</Setting>" +
                "</Settings>";
    }

    public boolean testServerUrl(String server) {
        try {
            URL url = new URL(server);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Device> getUserDevices(){
        Devices devices = Core.getDefaultClient().getLoggedInUser().getOwnedDevices();
        refreshDeviceCache(devices);
        return deviceCache;
    }

    public void refreshDeviceCache(Devices devices){
        for (Device device : deviceCache) {
            if (!devices.contains(device))
                deviceCache.remove(device);
        }

        for (Device device : devices) {
            if (!deviceCache.contains(device))
                deviceCache.add(device);
        }
    }

    public List<Device> getDeviceCache(){
        return deviceCache;
    }

    public void setCurrentDevice(Device currentDevice) {
        this.currentDevice = currentDevice;
    }

    public Device getCurrentDevice() {
        return currentDevice;
    }

    public void setAsyncMessageListener(AsyncMessageListener asyncMessageListener) {
        this.asyncMessageListener = asyncMessageListener;
    }


}
