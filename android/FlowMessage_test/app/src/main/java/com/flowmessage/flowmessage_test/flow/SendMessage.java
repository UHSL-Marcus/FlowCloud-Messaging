package com.flowmessage.flowmessage_test.flow;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.flowmessage.flowmessage_test.utils.AsyncResponse;
import com.imgtec.flow.Flow;
import com.imgtec.flow.FlowHandler;
import com.imgtec.flow.MessagingEvent;
import com.imgtec.flow.client.core.Core;
import com.imgtec.flow.client.users.Device;
import com.imgtec.flow.client.users.Devices;
import com.imgtec.flow.client.users.User;




/**
 * Created by Marcus on 15/02/2016.
 */
public class SendMessage extends AsyncTask<String, String, String> {

    public AsyncResponse delegate = null;
    FlowHandler flowHandler;
    Handler handler;
    User user;
    String errorMessage = "";


    public SendMessage(AsyncResponse asyncResponse, FlowHandler flowHandler, Handler handler, User user) {
        delegate = asyncResponse;
        this.flowHandler = flowHandler;
        this.handler = handler;
        this.user = user;
    }

    protected String doInBackground(String...info)
    {
        try {
            Devices devices = user.getAllDevices();
            if (devices != null && devices.size() == 0)
                return "board not online";
            else {

                for (int i = 0; i < devices.size(); i++) {
                    Device device = devices.get(i);
                    Flow.getInstance().subscribe(flowHandler, user.getFlowMessagingAddress().getAddress(),
                            MessagingEvent.MessagingEventCategory.FLOW_MESSAGING_EVENTCATEGORY_ASYNC_MESSAGE, "", 50, handler);

                    String[] addrList = new String[] {device.getFlowMessagingAddress().getAddress()};
                    Flow.getInstance().sendAsyncMessage(flowHandler, addrList, "Message");
                }


                return "Send Message";

            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            return "Getting devices failed (" + errorMessage + ")";
        }
    }

    protected void onProgressUpdate(String... values) {
        delegate.processMessage(values[0]);
    }

    protected void onPostExecute(String result)
    {
        delegate.processMessage(result);
    }
}
