package com.uhsl.flowmessage.flowmessagev2.flow;

import android.content.Context;
import android.os.AsyncTask;


import com.imgtec.flow.Flow;
import com.imgtec.flow.FlowHandler;
import com.imgtec.flow.client.core.API;
import com.imgtec.flow.client.core.Core;
import com.imgtec.flow.client.core.Client;
import com.imgtec.flow.client.core.ResourceCreatedResponse;
import com.imgtec.flow.client.users.User;
import com.imgtec.flow.client.users.UserHelper;
import com.imgtec.flow.client.users.Users;

import java.lang.reflect.Array;
import java.util.Locale;

/**
 * Created by Marcus on 12/02/2016.
 */

public class UserLogin extends AsyncTask<String, String, UserLogin.Result> {

    //public AsyncResponse delegate = null;
    private Context context;
    private String errorMessage = "";

    private Result result = new Result();

    String initXML = "<Settings><Setting><Name>restApiRoot</Name><Value>http://ws-uat.flowworld.coml</Value></Setting>" +
            "<Setting><Name>licenseeKey</Name><Value>Ph3bY5kkU4P6vmtT</Value></Setting>" +
            "<Setting><Name>licenseeSecret</Name><Value>Sd1SVBfYtGfQvUCR</Value></Setting>" +
            "<Setting><Name>configDirectory</Name><Value>/mnt/flowmessage_test/out-linux/bin/config</Value></Setting>" +
            "<Setting><Name>transportProtocol</Name><Value>TCP</Value></Setting></Settings>";

    String username = "mail+flow@marcuslee1.co.uk";
    String password = "Sm@rtlab1234";

    public UserLogin (Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Flow.getInstance().setAppContext(context);
    }

    protected Result doInBackground(String...info)
    {
        publishProgress("Starting...");

        if (Core.getDefaultClient().isUserLoggedIn()) {
            publishProgress("User Logged in");
            publishProgress("username: " + result.getUser().getUserName());
        }

        if (initFlowCore()){
            publishProgress("Init complete");

            if(userLogin(username, password)) {
                publishProgress("User Logged in");

            } else {
                publishProgress("FlowCore user login failed (" + errorMessage + ")");
            }

        }else {
            publishProgress("FlowCore init failed ( " + errorMessage + ")");
        }

        return result;
    }


    protected void onProgressUpdate(String... values) {
        System.out.println(values[0]);
    }

    protected void onPostExecute(UserLogin.Result result)
    {

    }

    private boolean initFlowCore() {
        boolean result = false;

        try {
            result = Flow.getInstance().init(initXML);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return result;
    }

    private boolean userLogin(String username, String password)
    {
        boolean result = false;

        try {
            User user = UserHelper.newUser(Core.getDefaultClient());
            FlowHandler handler = new FlowHandler();
            result = Flow.getInstance().userLogin(username, password, user, handler);
            if (result) {
                this.result.setSuccess(true);
                this.result.setUser(user);
                this.result.setFlowHandler(handler);
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        return result;

    }

    public class Result {
        private Boolean Success;
        private FlowHandler flowHandler;
        private User user;

        public Result() {
            setSuccess(false);
        }

        public Boolean getSuccess() {
            return Success;
        }

        private void setSuccess(Boolean success) {
            Success = success;
        }

        public FlowHandler getFlowHandler() {
            return flowHandler;
        }

        private void setFlowHandler(FlowHandler flowHandler) {
            this.flowHandler = flowHandler;
        }

        public User getUser() {
            return user;
        }

        private void setUser(User user) {
            this.user = user;
        }

    }
}