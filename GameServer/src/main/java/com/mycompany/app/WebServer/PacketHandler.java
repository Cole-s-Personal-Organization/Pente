package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.WebServer.Packet;

/**
 * This class will be used to standardize the format of messages. It will be a wrapper for a jackson JSON object 
 */
public class PacketHandler {
    // set as static to ensure all json mapping is being done with the same configs
    private static ObjectMapper objectMapper = getDefaultObjectMapper();

    private static ObjectMapper getDefaultObjectMapper() {
        return new ObjectMapper();
    }


    /**
     * Helper function to remove '\"' from either end of a string.
     * @param jsonStr
     * @return
     */
    public static String removeQuotesFromStringCastedJson(String jsonStr) {
        boolean isDoubleQuoted = jsonStr.startsWith("\"") && jsonStr.endsWith("\"");
        boolean isSingleQuoted = (jsonStr.startsWith("\"") && jsonStr.endsWith("\""));
        int strLen = jsonStr.length();

        if((isSingleQuoted || isDoubleQuoted) && strLen > 1) {
            return jsonStr.substring(1, strLen - 1);
        }
        return jsonStr;
    }


    public static Packet parsePacket(String rawPacket) throws InvalidPacketConstructionException {
        List<String> missingAttributes = new ArrayList<String>();

        JsonNode messageNode;

        try {
            messageNode = objectMapper.readTree(rawPacket);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidPacketConstructionException("Error: unreadable message recieved.");
        }

        JsonNode extractedNamespace = messageNode.get("namespace");
        JsonNode extractedEndpoint = messageNode.get("endpoint");
        JsonNode extractedSenderId = messageNode.get("senderId");
        JsonNode extractedRecipientList = messageNode.get("recipients");
        JsonNode extractedAction = messageNode.get("action");
        JsonNode extractedData = messageNode.get("data");


        String namespace = (extractedNamespace != null) 
            ? (removeQuotesFromStringCastedJson(extractedNamespace.toString())) 
            : (""); // default to empty namespace 
        String endpoint = (extractedEndpoint != null) 
            ? (removeQuotesFromStringCastedJson(extractedEndpoint.toString())) 
            : (""); // default to empty endpoint
        String action = (extractedAction != null) 
            ? (removeQuotesFromStringCastedJson(extractedAction.toString())) 
            : (null); 
        String senderId = (extractedSenderId != null) 
            ? (removeQuotesFromStringCastedJson(extractedSenderId.toString())) 
            : (null); 

        String[] recipientList = (extractedRecipientList != null) 
            ? (objectMapper.convertValue(extractedRecipientList, String[].class))
            : (new String[0]);
        for (int i = 0; i < recipientList.length; i++) {
            recipientList[i] = removeQuotesFromStringCastedJson(recipientList[i]);
        }

        // throw error if missing required info
        if (senderId == null) { missingAttributes.add("senderId"); }
        if (action == null) { missingAttributes.add("action"); }
        if (!missingAttributes.isEmpty()) {
            String missingAttributeString = missingAttributes.toString();
            throw new InvalidPacketConstructionException("Error: missing " + missingAttributeString + " attribute in raw message. Cannot construct Message object.");
        }
        
        return new Packet(namespace, endpoint, action, senderId, recipientList, extractedData);
    }

    public static JsonNode messageToJson(Packet message) {
        return objectMapper.valueToTree(message);
    }

    public static class InvalidPacketConstructionException extends Exception {
        public InvalidPacketConstructionException(String errorMessage) {
            super(errorMessage);
        }
    }
}
