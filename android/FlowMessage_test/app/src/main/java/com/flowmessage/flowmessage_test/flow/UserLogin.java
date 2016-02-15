package com.flowmessage.flowmessage_test.flow;

import android.os.AsyncTask;

import com.flowmessage.flowmessage_test.utils.AsyncResponse;
import com.imgtec.flow.Flow;
import com.imgtec.flow.FlowHandler;
import com.imgtec.flow.client.core.Core;
import com.imgtec.flow.client.core.Client;
import com.imgtec.flow.client.users.User;
import com.imgtec.flow.client.users.UserHelper;

import java.lang.reflect.Array;

/**
 * Created by Marcus on 12/02/2016.
 */

public class UserLogin extends AsyncTask<String, String, UserLogin.Result> {

    public AsyncResponse delegate = null;
    private String errorMessage = "";

    private Result result = new Result();

    String initXML = "<Settings><Setting><Name>restApiRoot</Name><Value>http://ws-uat.flowworld.com</Value></Setting>" +
            "<Setting><Name>licenseeKey</Name><Value>Ph3bY5kkU4P6vmtT</Value></Setting>" +
            "<Setting><Name>licenseeSecret</Name><Value>Sd1SVBfYtGfQvUCR</Value></Setting>" +
            //"<Setting><Name>configDirectory</Name><Value>/mnt/flowmessage_test/out-linux/bin/config</Value></Setting>" +
            "<Setting><Name>transportProtocol</Name><Value>TCP</Value></Setting></Settings>";

    String server = "http://ws-uat.flowworld.com";
    String oAuth = "Ph3bY5kkU4P6vmtT";
    String secret = "Sd1SVBfYtGfQvUCR";

    String username = "m.lee23@herts.ac.uk";
    String password = "Sm@rtlab1234";

    public UserLogin (AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    protected Result doInBackground(String...info)
    {
        publishProgress("Starting...");

        if (Core.getDefaultClient().isUserLoggedIn()) {
            publishProgress("User Logged in");
        }


        if (initFlowCore()){
            publishProgress("Init complete");

            if (setServerDetails(server, oAuth, secret)){
                publishProgress("Server Set");

                if(userLogin(username, password)) {
                    publishProgress("User Logged in");

                } else {
                    publishProgress("FlowCore user login failed (" + errorMessage + ")");
                }

            } else {
                publishProgress("FlowCore setServer failed (" + errorMessage + ")");
            }

        }else {
            publishProgress("FlowCore init failed ( " + errorMessage + ")");
        }

        if (!result.getSuccess())
            Flow.getInstance().shutdown();


        return result;
    }


    protected void onProgressUpdate(String... values) {
       delegate.processMessage(values[0]);
    }

    protected void onPostExecute(Boolean result)
    {
        delegate.processMessage(result);
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

    private boolean setServerDetails(String server, String oAuth, String secret)
    {
        try {
            Client cli = Core.getDefaultClient();
            cli.setServer(server, oAuth, secret);
        } catch (Exception e) {
            errorMessage = e.getMessage();
            return false;
        }
        return true;
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
            } else {
                errorMessage = "userLogin() failed. " + Flow.getInstance().toString();
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
