package com.flowmessage.flowmessage_test.activites;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.flowmessage.flowmessage_test.R;
import com.flowmessage.flowmessage_test.flow.SendMessage;
import com.flowmessage.flowmessage_test.flow.UserLogin;
import com.flowmessage.flowmessage_test.utils.AsyncResponse;
import com.imgtec.flow.FlowHandler;
import com.imgtec.flow.MessagingEvent;
import com.imgtec.flow.client.users.User;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("flowcore");
        System.loadLibrary("flowsip");
        System.loadLibrary("flow");
    }

    User _user;
    FlowHandler _fHandler;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button getDeviceButton = (Button) findViewById(R.id.getDevices_btn);
        getDeviceButton.setVisibility(View.GONE);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                System.out.println("message");
                MessagingEvent messagingEvent = (MessagingEvent)msg.obj;

                addInfoText("Message: '" + messagingEvent.content +
                        "' Sender: '" + messagingEvent.sender);
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addInfoText(String text)
    {
        TextView textView = (TextView) findViewById(R.id.info_text);
        textView.append(text + "\n");
    }

    public void doConnect(View view)
    {
        addInfoText("Sending...");
        doLogin();
    }

    public void doSendMessage(View view)
    {
        SendMessage sendMessage = new SendMessage(new AsyncResponse() {
            @Override
            public void processMessage(Object output) {
                if (output instanceof String)
                    addInfoText((String) output);
            }
        }, _fHandler, handler, _user);
        sendMessage.execute();
    }
    
    private void loginSuccessCallback(UserLogin.Result result){
        this._user = result.getUser();
        this._fHandler = result.getFlowHandler();

        ((Button) findViewById(R.id.login_btn)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.getDevices_btn)).setVisibility(View.VISIBLE);
    }

    public void doLogin()
    {
        UserLogin userLogin = new UserLogin(new AsyncResponse() {
            @Override
            public void processMessage(Object output) {
                if(output instanceof String)
                    addInfoText((String) output);

                if(output instanceof UserLogin.Result) {
                    UserLogin.Result result = (UserLogin.Result) output;
                    if (result.getSuccess())
                        loginSuccessCallback(result);
                }

                //if(output instanceof Boolean)
                    //if ((Boolean) output)
                       // loginSuccessCallback();
            }
        }, getApplicationContext());
        userLogin.execute("");
    }
}
