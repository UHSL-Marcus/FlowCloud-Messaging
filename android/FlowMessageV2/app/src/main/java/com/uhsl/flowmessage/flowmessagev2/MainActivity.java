package com.uhsl.flowmessage.flowmessagev2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;

import com.imgtec.flow.Flow;
import com.uhsl.flowmessage.flowmessagev2.flow.FlowController;
import com.uhsl.flowmessage.flowmessagev2.utils.ActivityController;

public class MainActivity extends AppCompatActivity  {

    private FlowController flowController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flowController = FlowController.getInstance(this);

        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        System.out.println("createMain");



    }

    public void doChooseDeviceActivity(View view) {
        startActivity(new Intent(this, ChooseDeviceActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.settings_logout_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_settings_item) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if(item.getItemId() == R.id.toolbar_logout_item) {
            flowController.logoutUser(true);
            if (!flowController.isUserLoggedIn()) {
                ActivityController.changeActivity(this, new Intent(this, LoginActivity.class));
            } else {
                System.out.println("logout failed");
            }
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
