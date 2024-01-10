package com.mycompany.app.WebServer;

import java.util.*;
import java.io.IOException;
import java.net.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Packet {

    // helps with ordering of incoming packets
    // private UUID sequenceNumber;

    // checksum for data integrity 
    // private int checksum;

    // destination address of logical handler, e.g. groups, game. 
    //      This will determine the location of the end interpreter used to handle the message.
    public final String[] namespacePath; 

    // /group1/team1/
    
    // defines the packet type
    //  within the namespace grouping, helps with identification/interpretation of data
    public final String command;     


    // NOTE: this should NEVER be sent over sockets, this data will be attached only on the server-side
    //       to help with processing
    public final InetAddress senderSocketAddress;


    // any attached data to the message
    //  UNPARSED, up to the reciever to make sense of it
    public final JsonNode data;



    private Packet(PacketBuilder builder) throws InvalidPacketConstructionException {
        List<String> missingAttributes = new ArrayList<String>(); // list used to collect the names of all missing attributes for error printing
        
        // throw error if missing required info
        if (builder.command == null) { missingAttributes.add("command"); }
        if (builder.namespace == null) { missingAttributes.add("namespace"); }
        if (builder.senderSocketAddress == null) { missingAttributes.add("senderSocketAddress"); }
        if (builder.data == null) { missingAttributes.add("data"); }
        if (!missingAttributes.isEmpty()) {
            String missingAttributeString = missingAttributes.toString();
            throw new InvalidPacketConstructionException("Error: missing " + missingAttributeString + " attribute in raw message. Cannot construct Message object.");
        }

        this.namespacePath = builder.namespace.split("/");
        this.command = builder.command;
        this.senderSocketAddress = builder.senderSocketAddress;
        this.data = builder.data;
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

    public static class PacketBuilder {
        // essential
        private String namespace; 
        private String command;     

        // optional
        private InetAddress senderSocketAddress;
        private JsonNode data;

        public PacketBuilder(String namespace, String command) {
            this.namespace = namespace;
            this.command = command;
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

        /**
         * Parses, with a jackson object mapper, packet information with 
         * This takes place in a constructor because this parse should only take place on initial object construction.
         * @param rawStringifiedPacket
         */
        public PacketBuilder(String rawStringifiedPacket) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode messageNode;
    
            try {
                messageNode = objectMapper.readTree(rawStringifiedPacket);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
    
            // expects only namespace, command, and data
            JsonNode extractedNamespace = messageNode.get("namespace");
            JsonNode extractedCommand = messageNode.get("command");
            JsonNode extractedData = messageNode.get("data");

            String namespace = (extractedNamespace != null) 
                ? (removeQuotesFromStringCastedJson(extractedNamespace.toString())) 
                : (null); 
            String command = (extractedCommand != null) 
                ? (removeQuotesFromStringCastedJson(extractedCommand.toString())) 
                : (null); 
    
            String stringifiedData = (extractedData != null) 
                ? removeQuotesFromStringCastedJson(extractedData.toString())
                : (null); 
            JsonNode dataNode = objectMapper.createObjectNode();
    
            try {
                dataNode = objectMapper.readTree(stringifiedData);
            } catch (IOException e) {
                System.out.println("Error: issue unwraping and reading data.");
                throw e;
            }
            
            this.namespace = namespace;
            this.command = command;
            this.data = dataNode;
        }

        public PacketBuilder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        } 

        public PacketBuilder setCommand(String command) {
            this.command = command;
            return this;
        }

        public PacketBuilder setSenderSocketAddress(InetAddress senderSocketAddress) {
            this.senderSocketAddress = senderSocketAddress;
            return this;
        }

        public PacketBuilder setData(JsonNode data) {
            this.data = data;
            return this;
        }

        public Packet build() throws InvalidPacketConstructionException {
            return new Packet(this);
        }
    }

    public static class InvalidPacketConstructionException extends Exception {
        public InvalidPacketConstructionException(String errorMessage) {
            super(errorMessage);
        }
    }
}
