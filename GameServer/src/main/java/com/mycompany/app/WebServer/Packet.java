package com.mycompany.app.WebServer;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;

public class Packet {

    // helps with ordering of incoming packets
    // private UUID sequenceNumber;

    // checksum for data integrity 
    // private int checksum;

    // destination address of logical handler, e.g. groups, game. 
    //      This will determine the location of the end interpreter used to handle the message.
    private String[] namespacePath; 
    
    // defines the packet type
    //  within the namespace grouping, helps with identification/interpretation of data
    private String command;     

    // any attached data to the message
    //  UNPARSED, up to the reciever to make sense of it
    private JsonNode data;



    public Packet(String namespacePath, String command, JsonNode data) {
        this.namespacePath = namespacePath.split("/");
        this.command = command;
        this.data = data;
    } 

    /**
     * Create a stringifyied namespace using '/' to separate paths
     * @return
     */
    public String getStringifiedNamespace() {
        if (this.namespacePath == null || this.namespacePath.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        result.append(this.namespacePath[0]);

        for (int i = 1; i < this.namespacePath.length; i++) {
            result.append("/").append(this.namespacePath[i]);
        }

        result.append("/"); // always append '/' to end

        return result.toString();
    }

    public String toPrettyPrintString() {
        return "Message: {\n" 
                + getStringifiedNamespace() + this.command + "\n"
                + "data: " + this.data.toString() + "\n"
                + "}";
    }

    public String toSendString() {
        String sendString = "{" 
            + "\"namespace\":\"" + getStringifiedNamespace() + "\","
            + "\"command\":\"" + this.command + "\","
            + "\"data\":\"" + this.data + "\"";
        return sendString + "}";
    }

    public String[] getNamespacePath() {
        return namespacePath;
    }

    public String getCommand() {
        return command;
    }

    public JsonNode getData() {
        return data;
    }
}
