package com.mycompany.app;

import static org.junit.Assert.*;

import java.util.*;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import org.junit.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.WebServer.Packet;

public class PacketTest {

    @Test
    public void testPacketCreation() throws UnknownHostException, IOException, Packet.InvalidPacketConstructionException {
        // Create a sample JSON data
        String jsonData = "{\"key\":\"value\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.readTree(jsonData);

        // Create a Packet using the PacketBuilder
        Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder("/group1/team1", "command", InetAddress.getLocalHost(), dataNode);
        Packet packet = packetBuilder.build();

        // Check if the packet is not null
        assertNotNull(packet);

        // Check if the namespace is correctly set
        assertEquals("/group1/team1/", packet.getStringifiedNamespace());

        // Check if the command is correctly set
        assertEquals("command", packet.command);

        // Check if the sender socket address is correctly set
        assertEquals(InetAddress.getLocalHost(), packet.senderSocketAddress);

        // Check if the data is correctly set
        assertEquals(dataNode, packet.data);
    }

    @Test(expected = Packet.InvalidPacketConstructionException.class)
    public void testInvalidPacketCreation() throws UnknownHostException, Packet.InvalidPacketConstructionException {
        // Try to create a packet with missing required attributes, should throw an exception
        Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder("/group1/team1", null, InetAddress.getLocalHost(), null);
        packetBuilder.build();
    }

    @Test
    public void testStringifyNamespace() throws UnknownHostException {
        // Create a Packet with a specific namespace
        Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder("/group1/team1", "command", InetAddress.getLocalHost(), null);
        Packet packet = null;
        try {
            packet = packetBuilder.build();
        } catch (Packet.InvalidPacketConstructionException e) {
            e.printStackTrace();
        }

        // Check if the stringifyNamespace function works as expected
        assertEquals("/group1/team1/", packet.getStringifiedNamespace());
    }

    @Test
    public void testToPrettyPrintString() throws UnknownHostException, IOException,  Packet.InvalidPacketConstructionException {
        // Create a sample JSON data
        String jsonData = "{\"key\":\"value\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.readTree(jsonData);

        // Create a Packet using the PacketBuilder
        Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder("/group1/team1", "command", InetAddress.getLocalHost(), dataNode);
        Packet packet = packetBuilder.build();

        // Check if the toPrettyPrintString function works as expected
        String expectedString = "Message: {\n/group1/team1/command\n" + "data: " + dataNode.toString() + "\n}";
        assertEquals(expectedString, packet.toPrettyPrintString());
    }

    @Test
    public void testToSendString() throws UnknownHostException, IOException, Packet.InvalidPacketConstructionException {
        // Create a sample JSON data
        String jsonData = "{\"key\":\"value\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.readTree(jsonData);

        // Create a Packet using the PacketBuilder
        Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder("/group1/team1", "command", InetAddress.getLocalHost(), dataNode);
        Packet packet = packetBuilder.build();

        // Check if the toSendString function works as expected
        String expectedString = "{\"namespace\":\"/group1/team1/\",\"command\":\"command\",\"data\":\"" + dataNode.toString() + "\"}";
        assertEquals(expectedString, packet.toSendString());
    }
}