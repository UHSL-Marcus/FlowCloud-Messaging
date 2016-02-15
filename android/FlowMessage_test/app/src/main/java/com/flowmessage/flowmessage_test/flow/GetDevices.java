package com.flowmessage.flowmessage_test.flow;

import android.os.AsyncTask;

import com.flowmessage.flowmessage_test.utils.AsyncResponse;
import com.imgtec.flow.client.core.Core;
import com.imgtec.flow.client.users.Device;
import com.imgtec.flow.client.users.Devices;
import com.imgtec.flow.client.users.User;

/**
 * Created by Marcus on 15/02/2016.
 */
public class GetDevices extends AsyncTask<String, String, String> {

    public AsyncResponse delegate = null;
    private String errorMessage = "";

    public GetDevices (AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    protected String doInBackground(String...info)
    {
        try {
            User user = Core.getDefaultClient().getLoggedInUser();
            Devices devices = user.getAllDevices();
            if (devices != null && devices.size() == 0)
                return "No Devices";
            else {
                String allDeviceInfo = "\n\n---Available Devices---";
                for (int i = 0; i < devices.size(); i++) {
                    Device device = devices.get(i);
                    allDeviceInfo += "\n--Device--" +
                            "\nName: " + device.getDeviceName() +
                            "\nIP Addr: " + device.getRemoteAccessIPAddress() +
                            "\nMAC Addr: " + device.getMACAddress() +
                            "\nSerial #: " + device.getDeviceID() +
                            "\n--End Device--";
                }
                allDeviceInfo += "\n---End---";

                return allDeviceInfo;

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
