package com.mycompany.app.WebServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

public class PacketHelpers {
    
    /**
     * A helper function for meant to standardize welcome packets
     * @param namespace
     * @return
     */
    public static Packet createWelcomePacket(String namespace) {
        Packet welcomePacket = null;

        try {
            welcomePacket = new Packet.PacketBuilder(namespace, "Hi There").build();
        } catch (Packet.InvalidPacketConstructionException e) {
            
        }

        return welcomePacket;
    } 

    /**
     * Error packet, includes an error message in packet data so that the response reciever knows what went wrong
     * @param failedPacket
     * @param errorMessage
     * @return
     */
    public static Packet createErrorPacket(Packet failedPacket, String errorMessage) {
        Packet errorPacket = null;

        JsonNode errorMessageData = new ObjectMapper().createObjectNode().put("errorMessage", errorMessage);

        try {
            errorPacket = new Packet.PacketBuilder(
                failedPacket.getStringifiedNamespace(), failedPacket.command)
                .setData(errorMessageData)
                .build();
        } catch (Packet.InvalidPacketConstructionException e) {
            
        }

        return errorPacket;

    }
}
