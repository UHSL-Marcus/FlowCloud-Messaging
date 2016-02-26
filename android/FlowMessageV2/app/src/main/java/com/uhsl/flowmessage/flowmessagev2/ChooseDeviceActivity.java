package com.uhsl.flowmessage.flowmessagev2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.imgtec.flow.client.users.Device;
import com.uhsl.flowmessage.flowmessagev2.flow.FlowController;
import com.uhsl.flowmessage.flowmessagev2.utils.ActivityController;
import com.uhsl.flowmessage.flowmessagev2.utils.AsyncCall;
import com.uhsl.flowmessage.flowmessagev2.utils.BackgroundTask;

import java.util.ArrayList;
import java.util.List;

public class ChooseDeviceActivity extends AppCompatActivity implements BackgroundTask.Callback<List<Device>> {

    private FlowController flowController;
    private Handler handler = new Handler();
    private Device selectedDevice;

    private Button connectBtn;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);

        flowController = FlowController.getInstance(this);

        setSupportActionBar((Toolbar) findViewById(R.id.choose_device_toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        connectBtn = (Button) findViewById(R.id.choose_device_connect_btn);
        connectBtn.setEnabled(false);

        listView = (ListView) findViewById(R.id.choose_device_listView);

        fillDeviceList();


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

    public void fillDeviceList() {
        selectedDevice = null;

        final ListView listView = (ListView) findViewById(R.id.choose_device_listView);


        ArrayList<String[]> placeholder = new ArrayList<String[]>();
        placeholder.add(new String[]{"No Devices", "", ""});

        listView.setAdapter(new ThreeLineOptionalHiddenArrayAdapter(this, placeholder, false, false, true));

        BackgroundTask.call(new AsyncCall<List<Device>>() {
            @Override
            public List<Device> call() {
                return flowController.requestDevices(true);
            }
        }, this, 3);
    }

    public void onBackGroundTaskResult(final List<Device> result, int task) {
        if (task == 3) {
            try {
                ArrayList<String[]> deviceList = new ArrayList<String[]>();
                for (Device device : result) {
                    deviceList.add(new String[]{device.getDeviceName(),
                            device.getFlowMessagingAddress().getAddress(), ""});
                }

                listView.setAdapter(new ThreeLineOptionalHiddenArrayAdapter(ChooseDeviceActivity.this, deviceList, false, false, true));
            } catch (Exception e) {
                System.out.println("get device exception: " + e.toString());
            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ThreeLineOptionalHiddenArrayAdapter.ViewHolder viewHolder =
                            (ThreeLineOptionalHiddenArrayAdapter.ViewHolder) view.getTag();

                    for (Device device : result) {
                        if (device.getFlowMessagingAddress().getAddress().equals(viewHolder.lineTwo.getText()))
                            selectedDevice = device;
                    }

                    if (selectedDevice != null)
                        connectBtn.setEnabled(true);
                }

            });

        }
    }

    public void doConnectToDevice(View view) {
        if (selectedDevice != null){
            flowController.setConnectedDevice(selectedDevice);
            this.startActivity(new Intent(this, MainActivity.class));
        }


    }
}
