package com.uhsl.flowmessage.flowmessagev2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.imgtec.flow.MessagingEvent;
import com.imgtec.flow.client.users.Device;
import com.uhsl.flowmessage.flowmessagev2.flow.AsyncMessageListener;
import com.uhsl.flowmessage.flowmessagev2.flow.FlowController;
import com.uhsl.flowmessage.flowmessagev2.utils.ActivityController;
import com.uhsl.flowmessage.flowmessagev2.utils.AsyncRun;
import com.uhsl.flowmessage.flowmessagev2.utils.BackgroundTask;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements AsyncMessageListener {

    private FlowController flowController;
    private List<String[]> messageList = new CopyOnWriteArrayList<>();
    private ArrayAdapter<String[]> messageListAdapter;
    private ListView messagesListView;
    private Handler handler = new Handler();

    private TextView connection_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flowController = FlowController.getInstance(this);
        flowController.setAsyncMessageListener(this);

        messageListAdapter =  new ThreeLineOptionalHiddenArrayAdapter(this, messageList, false, true, false);
        messagesListView = (ListView) findViewById(R.id.main_message_listView);
        messagesListView.setAdapter(messageListAdapter);


        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        connection_textView = (TextView) findViewById(R.id.main_connection_info_textView);



    }

    @Override
    protected void onResume(){
        super.onResume();

        setConnectionInfo();

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

    public void doChooseDeviceActivity(View view) {
        this.startActivity(new Intent(this, ChooseDeviceActivity.class));
    }

    private void setConnectionInfo() {
        Device device = flowController.getConnectedDevice();
        String info;
        if (device != null) {
            info = "Device Name:\n" + device.getDeviceName() + "\n";

            connection_textView.setText(info);
        }
        else {
            info = "No Device Connected";
        }

        connection_textView.setText(info);
    }

    private void addMessage(final String sender, final String content, final String status) {

        final String[] splitContent = content.split(Pattern.quote(";{MESSAGEID}"));

        handler.post(new Runnable() {
            @Override
            public void run() {
                messageList.add(new String[]{sender + ": " + splitContent[0], splitContent[1], status});
                messageListAdapter.notifyDataSetChanged();
                messagesListView.smoothScrollToPosition(messageListAdapter.getCount()-1);
            }
        });

    }

    @Override
    public void onMessageRecieved(MessagingEvent messagingEvent) {
        addMessage(messagingEvent.sender, messagingEvent.content, "");
    }

    @Override
    public void onResponseRecieved(MessagingEvent response) {
        String status;
        switch (response.messageResponse) {
            case SEND_SUCCESS:
                status = "Received"; break;
            case SENT_BUT_NOT_DELIVERED:
                status = "Sent"; break;
            case SEND_BUFFER_FULL:
                status = "Device Buffer Full"; break;
            case SEND_FAILED:
                status = "Failed"; break;
            default: status = ""; break;
        }

        String[] splitContent = response.content.split(Pattern.quote(";{MESSAGEID}"));

        for (String[] message : messageList) {
            if (message[1].equals(splitContent[1])) {
                message[2] = "Status: " + status;
                break;
            }
        }
        messageListAdapter.notifyDataSetChanged();

        System.out.println("Recieved response: " + response.sender + " : " + response.content
                + " : " + status);
    }

    public void doSendMessage(View view) {
        final String msg = ((EditText) findViewById(R.id.main_input_message_editText)).getText().toString()
                + ";{MESSAGEID}" + UUID.randomUUID().toString();
        final String recipent = flowController.getConnectedDevice().getFlowMessagingAddress().getAddress();

        BackgroundTask.run(new AsyncRun() {
            @Override
            public void run() {
                try {
                    if (flowController.sendAsyncMessage(recipent, msg))
                        addMessage("Me", msg, "Status: Sending...");
                    else addMessage("Me", "Message Failed;{MESSAGEID}0", "Status: Done");
                } catch (Exception e) {
                    System.out.println("Send message exception: " + e.toString() + " -> " + e.getMessage());
                }

            }
        });
    }






}
