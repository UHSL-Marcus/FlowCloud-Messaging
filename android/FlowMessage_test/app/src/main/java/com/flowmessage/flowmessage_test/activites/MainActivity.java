package com.flowmessage.flowmessage_test.activites;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.flowmessage.flowmessage_test.R;
import com.flowmessage.flowmessage_test.flow.UserLogin;
import com.flowmessage.flowmessage_test.utils.AsyncResponse;
import com.imgtec.flow.client.core.Client;
import com.imgtec.flow.client.core.Core;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("flowcore");
        System.loadLibrary("flowsip");
        System.loadLibrary("flow");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        textView.append(text +"\n");
    }

    public void doTest(View view)
    {
        addInfoText("Sending...");
        doLogin();
    }

    public void doLogin()
    {
        UserLogin userLogin = new UserLogin(new AsyncResponse() {
            @Override
            public void processMessage(Object output) {
                addInfoText((String) output);
            }
        });
        userLogin.execute("stuff");
    }
}