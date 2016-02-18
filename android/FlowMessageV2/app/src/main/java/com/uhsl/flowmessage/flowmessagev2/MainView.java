package com.uhsl.flowmessage.flowmessagev2;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileDescriptor;
import java.io.PrintWriter;


public class MainView extends Fragment {

    onLoginBtnClick loginBtnClick;

    public interface onLoginBtnClick {
        public void switchToLoginView();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view, container, false);
    }

    @Override
    public void onAttach {
        super.onAttach(activity);

    }
}
