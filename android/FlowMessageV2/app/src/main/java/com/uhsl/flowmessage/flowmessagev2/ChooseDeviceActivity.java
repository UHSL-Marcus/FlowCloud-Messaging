package com.uhsl.flowmessage.flowmessagev2;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ChooseDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);

        setSupportActionBar((Toolbar) findViewById(R.id.choose_device_toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Button connectBtn = (Button) findViewById(R.id.choose_device_connect_btn);

        final ListView listView = (ListView) findViewById(R.id.choose_device_listView);

        ArrayList<String[]> placeholder = new ArrayList<String[]>();
        placeholder.add(new String[] {"Board 1", "Addr:112233"});
        placeholder.add(new String[]{"Board 2", "Addr:0F8800"});

        listView.setAdapter(new ChooseDeviceArrayAdapter(this, placeholder));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //parent.setSelection(position);
                connectBtn.setEnabled(true);
            }

        });



    }




    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.settings_logout_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_settings_item) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
