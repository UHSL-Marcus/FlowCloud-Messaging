package com.uhsl.flowmessage.flowmessagev2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.imgtec.flow.Flow;
import com.uhsl.flowmessage.flowmessagev2.flow.FlowController;
import com.uhsl.flowmessage.flowmessagev2.utils.ActivityController;
import com.uhsl.flowmessage.flowmessagev2.utils.AsyncCall;
import com.uhsl.flowmessage.flowmessagev2.utils.BackgroundTask;
import com.uhsl.flowmessage.flowmessagev2.utils.ConfigSettings;

public class SettingsActivity extends AppCompatActivity implements BackgroundTask.Callback<Boolean> {

    private EditText serverEdit;
    private EditText oAuthKeyEdit;
    private EditText oAuthSecretEdit;

    private Button saveBtn;
    private Button reconnectBtn;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.settings_toolbar));


        saveBtn = (Button) findViewById(R.id.settings_save_btn);
        saveBtn.setEnabled(false);

        reconnectBtn = (Button) findViewById(R.id.settings_reconnect_btn);
        reconnectBtn.setEnabled(false);

        setEditTextViews();


    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.logout_only_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    /* View setup methods */

    private void setEditTextViews() {
        serverEdit = (EditText) findViewById(R.id.settings_server_address_editText);
        oAuthKeyEdit = (EditText) findViewById(R.id.settings_server_key_editText);
        oAuthSecretEdit = (EditText) findViewById(R.id.settings_sever_secret_editText);

        if (ConfigSettings.checkServerSettings(this)){
            serverEdit.setText(ConfigSettings.getStringSetting(this, ConfigSettings.SERVER));
            oAuthKeyEdit.setText(ConfigSettings.getStringSetting(this, ConfigSettings.OAUTH_KEY));
            oAuthSecretEdit.setText(ConfigSettings.getStringSetting(this, ConfigSettings.OAUTH_SECRET));
        }

        TextWatcher textWatcher = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                saveBtn.setEnabled(true);
                reconnectBtn.setEnabled(true);
            }
        };

        serverEdit.addTextChangedListener(textWatcher);
        oAuthKeyEdit.addTextChangedListener(textWatcher);
        oAuthSecretEdit.addTextChangedListener(textWatcher);

    }



    /* Listener callback methods */

    public void doSaveSettings(View view) {
        String server = serverEdit.getText().toString();
        String oAuthKey = oAuthKeyEdit.getText().toString();
        String oAuthSecret = oAuthSecretEdit.getText().toString();

        ConfigSettings.saveServerSettings(this, server, oAuthKey, oAuthSecret);

        saveBtn.setEnabled(false);

    }

    public void doReconnect(View view) {

        doSaveSettings(view);

        reconnectBtn.setText("Reconnecting...");

        BackgroundTask.call(new AsyncCall<Boolean>() {
            @Override
            public Boolean call() {
                FlowController flowController = FlowController.getInstance(SettingsActivity.this);
                flowController.reinitiliseFlow(SettingsActivity.this);
                System.out.println("Done return: " + flowController.isFlowInit());
                return flowController.isFlowInit();
            }
        }, this, 1);

    }


    public void onBackGroundTaskResult(Boolean result, int task) {
        if (task == 1) {
            reconnectBtn.setText("Reconnect");
            if (result) {
                System.out.println("recon callback true");
                ActivityController.changeActivity(this, new Intent(this, LoginActivity.class));
            } else {
                ActivityController.showSnackbarNoAction(findViewById(R.id.settings_coordinator_layout),
                        "Check API Settings", handler);
            }
        }

    }
}
