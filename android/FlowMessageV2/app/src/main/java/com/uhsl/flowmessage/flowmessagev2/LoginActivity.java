package com.uhsl.flowmessage.flowmessagev2;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.uhsl.flowmessage.flowmessagev2.flow.FlowController;
import com.uhsl.flowmessage.flowmessagev2.utils.ActivityController;
import com.uhsl.flowmessage.flowmessagev2.utils.AsyncRun;
import com.uhsl.flowmessage.flowmessagev2.utils.BackgroundTask;
import com.uhsl.flowmessage.flowmessagev2.utils.ConfigSettings;

public class LoginActivity extends AppCompatActivity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSupportActionBar((Toolbar) findViewById(R.id.login_toolbar));
        ActionBar actionBar = getSupportActionBar();

        if (true) { //TODO: check internet connectivity
            if (ConfigSettings.checkServerSettings(this))
                initaliseFlow(this);
            else {
                doSettingsSnackbar();
            }
        } else {
            //TODO: Snackbar -> no internet
        }

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.settings_only_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_settings_item) {
            ActivityController.changeActivity(this, new Intent(this, SettingsActivity.class));
            return true;
        } else
            return super.onOptionsItemSelected(item);
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
                            System.out.println(flowController.isFlowInit());
                            if (flowController.isFlowInit()) {
                                if (flowController.isUserLoggedIn())
                                    ActivityController.changeActivity(activity, new Intent(activity, MainActivity.class));

                            } else {
                                doSettingsSnackbar();
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

    private void doSettingsSnackbar() {

        ActivityController.showSnackbar(findViewById(R.id.login_coordinator_layout), "edit settings", "Settings",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityController.changeActivity(LoginActivity.this,
                                new Intent(LoginActivity.this, SettingsActivity.class));
                    }
                }, handler);
    }


    public void doLogin(View view) {
        //todo: timeout using post delayed. cancel future. remove callbacks after



    }
}
