package com.mycompany.app.WebServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * An object which exposes static functions which help the developer create standardized packets. 
 * 
 * @author Cole
 * @version 1.0.0
 */
public class PacketHelpers {
    
    /**
     * A helper function for meant to standardize welcome packets
     * @param namespace A namespace.
     * @return welcome packet
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
     * @param failedPacket A packet that failed in some capacity.
     * @param errorMessage A string explaining why the packet failed.
     * @return error packet
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
