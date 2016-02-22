package com.uhsl.flowmessage.flowmessagev2;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.imgtec.flow.client.core.FlowException;
import com.imgtec.flow.client.core.NetworkException;
import com.uhsl.flowmessage.flowmessagev2.flow.FlowController;
import com.uhsl.flowmessage.flowmessagev2.utils.AsyncCall;
import com.uhsl.flowmessage.flowmessagev2.utils.AsyncRun;
import com.uhsl.flowmessage.flowmessagev2.utils.BackgroundTask;
import com.uhsl.flowmessage.flowmessagev2.utils.ConfigSettings;

public class StarterActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (true) { //TODO: check internet connectivity
            if (ConfigSettings.checkServerSettings(this))
                initaliseFlow(this);
            else {
                this.startActivity(new Intent(this, SettingsActivity.class));
                //TODO: add snackbar
            }
        } else {
            //TODO: Snackbar -> no internet
        }
    }

    private void initaliseFlow(final Activity activity) {
        (new BackgroundTask(handler)).run(new AsyncRun() {
            @Override
            public void run() {
                try {
                    FlowController flowController = FlowController.getInstance(activity);
                    flowController.initFlowIfNot(activity);
                    if (flowController.isFlowInit()) {
                        System.out.println("Flow Started");
                        //TODO: start app
                    } else { System.out.println("Not Started"); }
                } catch (Exception e) {
                    System.out.println("Exception: " + e.toString() + " -> " + e.getMessage());
                    //TODO: Something else has gone wrong
                }
            }

        });

    }
}
