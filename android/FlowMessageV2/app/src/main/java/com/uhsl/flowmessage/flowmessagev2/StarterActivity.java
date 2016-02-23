package com.uhsl.flowmessage.flowmessagev2;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.imgtec.flow.client.core.FlowException;
import com.imgtec.flow.client.core.NetworkException;
import com.imgtec.voip.AlarmBroadCastReceiver;
import com.uhsl.flowmessage.flowmessagev2.flow.FlowController;
import com.uhsl.flowmessage.flowmessagev2.utils.ActivityController;
import com.uhsl.flowmessage.flowmessagev2.utils.AsyncCall;
import com.uhsl.flowmessage.flowmessagev2.utils.AsyncRun;
import com.uhsl.flowmessage.flowmessagev2.utils.BackgroundTask;
import com.uhsl.flowmessage.flowmessagev2.utils.ConfigSettings;

public class StarterActivity extends AppCompatActivity implements BackgroundTask.Callback<Boolean> {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (true) { //TODO: check internet connectivity
            if (ConfigSettings.checkServerSettings(this))
                initaliseFlow(this);
            else {
                goToSettings();
            }
        } else {
            //TODO: Snackbar -> no internet
        }
    }

    private void initaliseFlow(final Activity activity) {

        BackgroundTask.run(new AsyncRun() {
            @Override
            public void run() {
                try {
                    final FlowController flowController = FlowController.getInstance(activity);
                    flowController.initFlowIfNot(activity);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (flowController.isFlowInit()) {
                                if (flowController.isUserLoggedIn()) {
                                    activity.startActivity(new Intent(activity, MainActivity.class));
                                } else {
                                    activity.startActivity(new Intent(activity, LoginActivity.class));
                                }
                            } else {
                                goToSettings();

                            }

                        }
                    });


                } catch (Exception e) {
                    System.out.println("Exception: " + e.toString() + " -> " + e.getMessage());
                    //TODO: Something else has gone wrong
                }
            }

        });

    }

    private void goToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("snack", true);
        this.startActivity(intent);
    }

    @Override
    public void onBackGroundTaskResult(Boolean aBoolean, int task) {
        System.out.println("finished");
    }
}
