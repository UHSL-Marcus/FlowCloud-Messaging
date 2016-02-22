package com.uhsl.flowmessage.flowmessagev2;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.uhsl.flowmessage.flowmessagev2.utils.ConfigSettings;

public class SettingsActivity extends AppCompatActivity {

    private EditText serverEdit;
    private EditText oAuthKeyEdit;
    private EditText oAuthSecretEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.settings_toolbar));

        serverEdit = (EditText) findViewById(R.id.settings_server_address_editText);
        oAuthKeyEdit = (EditText) findViewById(R.id.settings_server_key_editText);
        oAuthSecretEdit = (EditText) findViewById(R.id.settings_sever_secret_editText);

        if (ConfigSettings.checkServerSettings(this)){
            //serverEdit.setText(ConfigSettings.getStringSetting(this, ConfigSettings.SERVER));
            //oAuthKeyEdit.setText(ConfigSettings.getStringSetting(this, ConfigSettings.OAUTH_KEY));
            //oAuthSecretEdit.setText(ConfigSettings.getStringSetting(this, ConfigSettings.OAUTH_SECRET));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.logout_only_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void doSaveSettings(View view) {
        String server = serverEdit.getText().toString();
        String oAuthKey = oAuthKeyEdit.getText().toString();
        String oAuthSecret = oAuthSecretEdit.getText().toString();

        ConfigSettings.saveServerSettings(this, server, oAuthKey, oAuthSecret);

    }

    public void doReconnect(View view) {
        this.startActivity(new Intent(this, SettingsActivity.class));
    }
}
