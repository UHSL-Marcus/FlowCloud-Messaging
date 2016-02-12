package com.flowmesage.flowmessage_test.flow;

import android.app.Activity;
import android.os.AsyncTask;

import com.flowmesage.flowmessage_test.utils.AsyncResponse;
import com.imgtec.flow.client.core.Core;

/**
 * Created by Marcus on 12/02/2016.
 */

public class UserLogin extends AsyncTask<String, Void, String> {


    public AsyncResponse delegate = null;
    private String errorMessage = "";

    public UserLogin (AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    protected String doInBackground(String...info)
    {
        if (initFlowCore()){
            return "Init complete";
        }else {
            return "FlowCore init failed ( " + errorMessage + ")";
        }
    }

    protected void onPostExecute(String result)
    {
        delegate.processFinish(result);
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
}
