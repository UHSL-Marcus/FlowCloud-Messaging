package com.uhsl.flowmessage.flowmessagev2.flow;

import android.content.Context;

import com.imgtec.flow.Flow;
import com.imgtec.flow.FlowHandler;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Marcus on 19/02/2016.
 */
public class FlowConnection {

    private static volatile FlowConnection instance;

    private Flow flowInstance;
    private FlowHandler userFlowHandler;
    private String username;
    private String password;

    private FlowConnection(Context context) {
        flowInstance = Flow.getInstance();
        flowInstance.setAppContext(context);
        userFlowHandler = new FlowHandler();
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


}
