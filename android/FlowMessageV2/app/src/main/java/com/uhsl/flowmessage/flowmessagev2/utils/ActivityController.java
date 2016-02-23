package com.uhsl.flowmessage.flowmessagev2.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Marcus on 22/02/2016.
 */
public class ActivityController {

    public static void changeActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.finish();
    }

    public static void showSnackbarNoActionFromBackground(final View view, final String message, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public static void showSnackbarNoAction(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
