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
        List<String> missingAttributes = new ArrayList<String>(); // list used to collect the names of all missing attributes for error printing

        JsonNode messageNode;

        try {
            messageNode = objectMapper.readTree(rawPacket);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidPacketConstructionException("Error: unreadable message recieved.");
        }

        JsonNode extractedNamespace = messageNode.get("namespace");
        JsonNode extractedCommand = messageNode.get("command");
        JsonNode extractedData = messageNode.get("data");
        JsonNode extractedValidationSignature = messageNode.get("validatedUser");

        String namespace = (extractedNamespace != null) 
            ? (removeQuotesFromStringCastedJson(extractedNamespace.toString())) 
            : (null); 
        String command = (extractedCommand != null) 
            ? (removeQuotesFromStringCastedJson(extractedCommand.toString())) 
            : (null); 
        Boolean validationSig = (extractedValidationSignature != null) 
            ? Boolean.valueOf(removeQuotesFromStringCastedJson(extractedValidationSignature.toString())) 
            : (null); 

        String stringifiedData = (extractedData != null) 
            ? removeQuotesFromStringCastedJson(extractedData.toString())
            : (null); 
        JsonNode dataNode = objectMapper.createObjectNode();

        try {
            dataNode = objectMapper.readTree(stringifiedData);
        } catch (Exception e) {
            System.out.println("Error: issue unwraping and reading data.");
        }

        // throw error if missing required info
        if (command == null) { missingAttributes.add("command"); }
        if (namespace == null) { missingAttributes.add("namespace"); }
        if (!missingAttributes.isEmpty()) {
            String missingAttributeString = missingAttributes.toString();
            throw new InvalidPacketConstructionException("Error: missing " + missingAttributeString + " attribute in raw message. Cannot construct Message object.");
        }

        Packet packet;

        if (validationSig != null) {
            packet = new Packet(namespace, command, dataNode, validationSig);
        } else {
            packet = new Packet(namespace, command, dataNode);
        }
        
        return packet;
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
