package com.uhsl.flowmessage.flowmessagev2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null && savedInstanceState != null) {
            MainView mainView = new MainView();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mainView).commit();
        }
    }

    public void switchToLoginView(){

    }
}
