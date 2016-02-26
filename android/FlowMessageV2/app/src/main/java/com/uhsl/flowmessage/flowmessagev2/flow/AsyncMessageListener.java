package com.uhsl.flowmessage.flowmessagev2.flow;

import android.os.Message;

import com.imgtec.flow.MessagingEvent;

/**
 * Created by Marcus on 26/02/2016.
 */
public interface AsyncMessageListener {
    void onMessageRecieved(MessagingEvent messagingEvent);

    void onResponseRecieved(MessagingEvent response);
}
