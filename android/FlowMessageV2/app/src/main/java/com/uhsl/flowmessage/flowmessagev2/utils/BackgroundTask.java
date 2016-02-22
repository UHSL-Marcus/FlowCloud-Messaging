package com.uhsl.flowmessage.flowmessagev2.utils;

import android.os.AsyncTask;
import android.os.Handler;

/**
 * Created by Marcus on 22/02/2016.
 */
public class BackgroundTask {

    private Handler handler;

    public BackgroundTask(Handler handler ) {
        this.handler = handler;
    }

    public void run(final AsyncRun asyncRun){
        handler.post(new Runnable() {
            @Override
            public void run() {
                asyncRun.run();
            }
        });

    }


}
