package com.uhsl.flowmessage.flowmessagev2.utils;

/**
 * Created by Marcus on 02/03/2016.
 */
public class MessageFormat {
    public String messageID;
    public String senderID;
    public String senderType;
    public String type;
    public String body;

    public MessageFormat() {};

    public MessageFormat(String messageID, String senderID, String senderType, String type, String body) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.senderType = senderType;
        this.type = type;
        this.body =body;
    }
}
