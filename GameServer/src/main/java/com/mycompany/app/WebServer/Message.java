package com.mycompany.app.WebServer;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;

public class Message {

    public final String namespace;      // defined grouping of endpoint collections, e.g. groups, game. This will determine the protocol used to handle the message.
    public final String command;       // within the namespace grouping, define endpoint for actions to be handled
    // public final MessageAction action;  // tell the reciever how to handle data, this is also namespace/context dependent. e.g. get, delete, update
    
    
    // public final String[] recipientIds; // allow direct addresses to clients using their ids
    // public final String senderId; 
    
    
    public final JsonNode data;         // any attached data to the message, UNPARSED, up to the reciever to make sense of it


    // public enum MessageAction {
    //     GET,
    //     POST,
    //     PUT,
    //     DELETE,
    //     UPDATE
    // }

    // a helper data structure to simplify the process of mapping string actions to their enum counterparts
    // public static final Map<String, MessageAction> actionStrToEnumMap = new HashMap<>();
    // static {
    //     actionStrToEnumMap.put("GET", MessageAction.GET);
    //     actionStrToEnumMap.put("POST", MessageAction.POST);
    //     actionStrToEnumMap.put("PUT", MessageAction.PUT);
    //     actionStrToEnumMap.put("DELETE", MessageAction.DELETE);
    //     actionStrToEnumMap.put("UPDATE", MessageAction.UPDATE);
    // }

    public Message(String namespace, String endpoint, String action, JsonNode data) {
        this.namespace = namespace;
        this.endpoint = this.formatEndpoint(endpoint);
        this.senderId = senderId;
        this.recipientIds = recipientIds;
        this.data = data;

        this.action = actionStrToEnumMap.get(action);
    } 

    /**
     * Incoming endpoints in this system will be allowed to be formatted in multiple ways.
     * They will need to be formatted to standardize the input though.
     *  Sample endpoint that will all be considered the same: 
     *      - chat/
     *      - chat
     *      - /chat/
     *      - /chat
     * @param rawEndpoint
     * @return
     */
    private String formatEndpoint(String rawEndpoint) {
        String formattedEndpoint = rawEndpoint;

        if (formattedEndpoint.startsWith("/")) {
            formattedEndpoint = formattedEndpoint.substring(1);
        }
        if(formattedEndpoint.length() > 1 && formattedEndpoint.endsWith("/")) {
            formattedEndpoint = formattedEndpoint.substring(0, formattedEndpoint.length() - 1);
        }

        return formattedEndpoint;
    }

    public String toPrettyPrintString() {
        return "Message: {\n" 
                + this.action.toString() + " " + this.namespace + "/" + this.endpoint + "\n"
                + "from: " + this.senderId + "\n"
                + "to: " + this.recipientIds + "\n"
                + "data: " + this.data.toString() + "\n"
                + "}";
    }

    public String toSendString() {
        String sendString = "{" 
            + "\"namespace\":\"" + this.namespace + "\","
            + "\"endpoint\":\"" + this.endpoint + "\","
            + "\"senderId\":\"" + this.senderId + "\","
            + "\"action\":\"" + this.action + "\","
            + "\"data\":\"" + this.data + "\"";
        
        // add recipient id's
        sendString += ",\"recipients\":[";
        for (int i = 0; i < recipientIds.length; i++) {
            sendString += "\"" + recipientIds[i] + "\"";
            if (i < recipientIds.length - 1) {
                sendString += ",";
            }
        }
        sendString += "]";


        return sendString + "}";
    }
}
