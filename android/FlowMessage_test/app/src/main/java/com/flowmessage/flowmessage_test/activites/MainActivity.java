package com.flowmessage.flowmessage_test.activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.flowmessage.flowmessage_test.R;
import com.flowmessage.flowmessage_test.flow.GetDevices;
import com.flowmessage.flowmessage_test.flow.UserLogin;
import com.flowmessage.flowmessage_test.utils.AsyncResponse;
import com.imgtec.flow.Flow;
import com.imgtec.flow.FlowHandler;
import com.imgtec.flow.client.users.User;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("flowcore");
        System.loadLibrary("flowsip");
        System.loadLibrary("flow");
    }

    User _user;
    FlowHandler _fHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button getDeviceButton = (Button) findViewById(R.id.getDevices_btn);
        getDeviceButton.setVisibility(View.GONE);

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

    public void doGetDevices(View view)
    {
        GetDevices getDevices = new GetDevices(new AsyncResponse() {
            @Override
            public void processMessage(Object output) {
                if (output instanceof String)
                    addInfoText((String) output);
            }
        });
        getDevices.execute();
    }
    
    private void loginSuccessCallback(UserLogin.Result result){
        this._user = result.getUser();
        this._fHandler = result.getFlowHandler();

        ((Button) findViewById(R.id.login_btn)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.getDevices_btn)).setVisibility(View.VISIBLE);
    }

    public void doLogin()
    {
        System.out.println("Last Error: " + Flow.getInstance().getLastError());
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
        });
        userLogin.execute("stuff");
    }
}
