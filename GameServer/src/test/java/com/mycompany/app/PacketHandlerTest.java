package com.mycompany.app;

import static org.junit.Assert.*;

import java.util.HashMap;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mycompany.app.WebServer.Packet;
import com.mycompany.app.WebServer.PacketHandler;

public class PacketHandlerTest {

    @Test
    public void testParsePacketWithoutValidationSignature() throws PacketHandler.InvalidPacketConstructionException {
        String rawPacket = "{\"namespace\":\"group1/team1\",\"command\":\"testCommand\",\"data\":\"{}\"}";

        Packet packet = PacketHandler.parsePacket(rawPacket);

        assertEquals("group1/team1/", packet.getStringifiedNamespace());
        assertEquals("testCommand", packet.getCommand());
        assertFalse(packet.getSenderValidated());
        assertEquals(new ObjectMapper().createObjectNode(), packet.getData());
    }

    @Test
    public void testParsePacketWithValidationSignature() throws PacketHandler.InvalidPacketConstructionException {
        String rawPacket = "{\"namespace\":\"group1/team1\",\"command\":\"testCommand\",\"data\":\"{}\",\"validatedUser\":\"true\"}";

        Packet packet = PacketHandler.parsePacket(rawPacket);

        assertEquals("group1/team1/", packet.getStringifiedNamespace());
        assertEquals("testCommand", packet.getCommand());
        assertTrue(packet.getSenderValidated());
        assertEquals((JsonNode)(new ObjectMapper().createObjectNode()), packet.getData());
    }

    @Test(expected = PacketHandler.InvalidPacketConstructionException.class)
    public void testParsePacketMissingNamespace() throws PacketHandler.InvalidPacketConstructionException {
        String rawPacket = "{\"command\":\"testCommand\",\"data\":\"{}\"}";

        PacketHandler.parsePacket(rawPacket);
    }

    @Test(expected = PacketHandler.InvalidPacketConstructionException.class)
    public void testParsePacketMissingCommand() throws PacketHandler.InvalidPacketConstructionException {
        String rawPacket = "{\"namespace\":\"group1/team1\",\"data\":\"{}\"}";

        PacketHandler.parsePacket(rawPacket);
    }

    // @Test
    // public void testMessageToJson() {
    //     String namespacePath = "group1/team1/";
    //     String command = "testCommand";
    //     JsonNode data = new ObjectMapper().createObjectNode();
    //     Boolean senderValidated = true;

    //     Packet packet = new Packet(namespacePath, command, data, senderValidated);

    //     JsonNode jsonNode = PacketHandler.messageToJson(packet);

    //     assertEquals(namespacePath, jsonNode.get("namespace").asText());
    //     assertEquals(command, jsonNode.get("command").asText());
    //     assertEquals(senderValidated, jsonNode.get("validatedUser").asBoolean());
    //     assertEquals(data.toString(), jsonNode.get("data").toString());
    // }
}
