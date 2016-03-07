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
import com.uhsl.flowmessage.flowmessagev2.utils.MessageBuilder;
import com.uhsl.flowmessage.flowmessagev2.utils.MessageFormat;
import com.uhsl.flowmessage.flowmessagev2.utils.MessageTypes;
import com.uhsl.flowmessage.flowmessagev2.utils.SenderTypes;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements AsyncMessageListener {

    private FlowController flowController;
    private List<String[]> messageList = new CopyOnWriteArrayList<>(); //[0]SenderID, [1]Body, [2]Status
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
        if (messagesListView.getAdapter() != null) {
            System.out.println("set adapter: " + messagesListView.getAdapter().toString());
        }
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

        final MessageFormat message = MessageBuilder.parseMessage(content);

        try {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    messageList.add(new String[]{sender + ": " + message.body, message.messageID, status});
                    messageListAdapter.notifyDataSetChanged();
                    messagesListView.smoothScrollToPosition(messageListAdapter.getCount() - 1);
                }
            });
        } catch (NullPointerException e) {
            System.out.println("malformed message");
        }

    }

    @Override
    public void onMessageRecieved(MessagingEvent messagingEvent) {
        System.out.println("MESSAGE UIGJK");
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

        MessageFormat newMessage = MessageBuilder.parseMessage(response.content);

        try {

            for (String[] message : messageList) {
                if (message[1].equals(newMessage.messageID)) {
                    message[2] = "Status: " + status;
                    break;
                }
            }
            messageListAdapter.notifyDataSetChanged();

            System.out.println("Recieved response: " + response.sender + " : " + response.content
                    + " : " + status);
        } catch (NullPointerException e) {
            System.out.println("malformed message: response callback");
        }
    }

    public void doSendMessage(View view) {

        BackgroundTask.run(new AsyncRun() {
            @Override
            public void run() {
                try {
                    MessageFormat newMessage = new MessageFormat(
                            UUID.randomUUID().toString(),
                            flowController.getUserID(), SenderTypes.USER, MessageTypes.TEXT_MESSAGE,
                            ((EditText) findViewById(R.id.main_input_message_editText)).getText().toString());


                    String msg = MessageBuilder.buildMessage(newMessage);
                    String recipent = flowController.getConnectedDevice().getFlowMessagingAddress().getAddress();


                    if (msg != null && flowController.sendAsyncMessage(recipent, msg))
                        addMessage("Me", msg, "Status: Sending...");
                    else addMessage("Me", buildFailedMessage(), "Status: Done");
                } catch (Exception e) {
                    System.out.println("Send message exception: " + e.toString() + " -> " + e.getMessage());
                }

            }
        });
    }


    private String buildFailedMessage() {
        MessageFormat newMessage = new MessageFormat("0", "0", "0", "0", "Message Failed");

        return MessageBuilder.buildMessage(newMessage);
    }






}
