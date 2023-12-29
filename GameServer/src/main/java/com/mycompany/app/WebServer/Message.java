package com.mycompany.app.WebServer;

import com.fasterxml.jackson.databind.JsonNode;

public class Message {

    public final String namespace;      // 
    public final String action;         // tell the reciever how to handle data, this is also namespace/context dependent. e.g. update
    public final String[] recipientIds; // allow direct addresses to clients using their ids
    public final String senderId; 
    public final JsonNode data;         // any attached data to the message, UNPARSED, up to the reciever to make sense of it
    

    public Message(String namespace, String action, String senderId, String[] recipientIds, JsonNode data) {
        this.namespace = namespace;
        this.action = action;
        this.senderId = senderId;
        this.recipientIds = recipientIds;
        this.data = data;
    } 
}
