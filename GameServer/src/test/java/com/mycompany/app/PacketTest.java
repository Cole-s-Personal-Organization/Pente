package com.mycompany.app;

import static org.junit.Assert.*;

import java.util.*;
import java.util.HashMap;
import org.junit.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.WebServer.Packet;

public class PacketTest {

    @Test
    public void testPacketConstructorWithoutSenderValidation() {
        String namespacePath = "group1/team1/";
        String command = "testCommand";
        JsonNode data = new ObjectMapper().createObjectNode();

        Packet packet = new Packet(namespacePath, command, data);

        assertArrayEquals(namespacePath.split("/"), packet.getNamespacePath());
        assertEquals(command, packet.getCommand());
        assertFalse(packet.getSenderValidated());
        assertEquals(data, packet.getData());
    }

    @Test
    public void testPacketConstructorWithSenderValidation() {
        String namespacePath = "group1/team1/";
        String command = "testCommand";
        JsonNode data = new ObjectMapper().createObjectNode();
        Boolean senderValidated = true;

        Packet packet = new Packet(namespacePath, command, data, senderValidated);

        assertArrayEquals(namespacePath.split("/"), packet.getNamespacePath());
        assertEquals(command, packet.getCommand());
        assertTrue(packet.getSenderValidated());
        assertEquals(data, packet.getData());
    }

    @Test
    public void testGetStringifiedNamespace() {
        String namespacePath = "group1/team1/";
        String command = "testCommand";
        JsonNode data = new ObjectMapper().createObjectNode();

        Packet packet = new Packet(namespacePath, command, data);

        assertEquals(namespacePath, packet.getStringifiedNamespace());
    }

    @Test
    public void testToPrettyPrintString() {
        String namespacePath = "group1/team1/";
        String command = "testCommand";
        JsonNode data = new ObjectMapper().createObjectNode();

        Packet packet = new Packet(namespacePath, command, data);

        String expected = "Message: {\n" 
                        + namespacePath + command + "\n"
                        + "data: " + data.toString() + "\n"
                        + "}";

        assertEquals(expected, packet.toPrettyPrintString());
    }

    @Test
    public void testToSendString() {
        String namespacePath = "group1/team1/";
        String command = "testCommand";
        JsonNode data = new ObjectMapper().createObjectNode();
        Boolean senderValidated = true;

        Packet packet = new Packet(namespacePath, command, data, senderValidated);

        String expected = "{" 
                        + "\"namespace\":\"" + namespacePath + "\","
                        + "\"command\":\"" + command + "\","
                        + "\"validatedUser\":\"" + senderValidated + "\","
                        + "\"data\":\"" + data + "\"}";

        assertEquals(expected, packet.toSendString());
    }
}