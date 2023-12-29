package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.WebServer.Message;

/**
 * This class will be used to standardize the format of messages. It will be a wrapper for a jackson JSON object 
 */
public class MessageHandler {
    // set as static to ensure all json mapping is being done with the same configs
    private static ObjectMapper objectMapper = getDefaultObjectMapper();

    private static ObjectMapper getDefaultObjectMapper() {
        return new ObjectMapper();
    }


    public static String removeQuotesFromStringCastedJson(String jsonStr) {
        boolean isDoubleQuoted = jsonStr.startsWith("\"") && jsonStr.endsWith("\"");
        boolean isSingleQuoted = (jsonStr.startsWith("\"") && jsonStr.endsWith("\""));
        int strLen = jsonStr.length();

        if((isSingleQuoted || isDoubleQuoted) && strLen > 1) {
            return jsonStr.substring(1, strLen - 1);
        }
        return jsonStr;
    }


    public static Message parseMessage(String rawMessage) throws InvalidMessageConstructionException {
        List<String> missingAttributes = new ArrayList<String>();

        JsonNode messageNode;

        try {
            messageNode = objectMapper.readTree(rawMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidMessageConstructionException("Error: unreadable message recieved.");
        }

        JsonNode extractedNamespace = messageNode.get("namespace");
        JsonNode extractedSenderId = messageNode.get("senderId");
        JsonNode extractedRecipientList = messageNode.get("recipients");
        JsonNode extractedAction = messageNode.get("action");
        JsonNode extractedData = messageNode.get("data");


        String namespace = (extractedNamespace != null) 
            ? (removeQuotesFromStringCastedJson(extractedNamespace.toString())) 
            : (""); // default to empty namespace 
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
            throw new InvalidMessageConstructionException("Error: missing " + missingAttributeString + " attribute in raw message. Cannot construct Message object.");
        }
        
        return new Message(namespace, action, senderId, recipientList, extractedData);
    }

    public static JsonNode messageToJson(Message message) {
        return objectMapper.valueToTree(message);
    }

    public static class InvalidMessageConstructionException extends Exception {
        public InvalidMessageConstructionException(String errorMessage) {
            super(errorMessage);
        }
    }
}
