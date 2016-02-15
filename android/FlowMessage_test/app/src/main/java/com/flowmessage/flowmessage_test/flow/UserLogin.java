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

public class UserLogin extends AsyncTask<String, String, Boolean> {

    public AsyncResponse delegate = null;
    private String errorMessage = "";

    String server = "http://ws-uat.flowworld.com";
    String oAuth = "Ph3bY5kkU4P6vmtT";
    String secret = "Sd1SVBfYtGfQvUCR";

    String username = "m.lee23@herts.ac.uk";
    String password = "Sm@rtlab1234";

    public UserLogin (AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    protected Boolean doInBackground(String...info)
    {
        Boolean result = false;
        publishProgress("Starting...");

        if (Core.getDefaultClient().isUserLoggedIn()) {
            publishProgress("User Logged in");
            return false;
        }


        if (initFlowCore()){
            publishProgress("Init complete");

            if (setServerDetails(server, oAuth, secret)){
                publishProgress("Server Set");

                if(userLogin(username, password)) {
                    publishProgress("User Logged in");
                    result = true;

                } else {
                    publishProgress("FlowCore user login failed (" + errorMessage + ")");
                }

            } else {
                publishProgress("FlowCore setServer failed (" + errorMessage + ")");
            }

        }else {
            publishProgress("FlowCore init failed ( " + errorMessage + ")");
        }

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
            result = Core.init();
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
            Client cli = Core.getDefaultClient();
            cli.loginAsUser(username, password, false);
            result = true;
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return result;

    }
}
