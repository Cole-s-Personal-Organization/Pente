package com.mycompany.app;

import static org.junit.Assert.*;

import java.util.*;
import java.io.IOException;
import java.net.*;

import org.junit.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.WebServer.Packet;

public class PacketTest {
    @Test
    public void testValidPacketConstruction() throws Packet.InvalidPacketConstructionException {
        String namespace = "/group1/team1/";
        String command = "move";
        UUID clientSessionId = UUID.randomUUID();
        JsonNode data = new ObjectMapper().createObjectNode();

        Packet.PacketBuilder builder = new Packet.PacketBuilder(namespace, command)
                .setClientSessionId(clientSessionId)
                .setData(data);

        Packet packet = builder.build();

        assertArrayEquals(namespace.split("/"), packet.namespacePath);
        assertEquals(command, packet.command);
        assertEquals(clientSessionId, packet.clientSessionId);
        assertEquals(data, packet.data);
    }

    @Test
    public void testPacketStringification() throws Packet.InvalidPacketConstructionException {
        String namespace = "/group1/team1/";
        String command = "move";
        UUID clientSessionId = UUID.randomUUID();
        JsonNode data = new ObjectMapper().createObjectNode();

        Packet.PacketBuilder builder = new Packet.PacketBuilder(namespace, command)
                .setClientSessionId(clientSessionId)
                .setData(data);

        Packet packet = builder.build();

        String expectedSendString = "{\"namespace\":\"" + namespace + "\",\"command\":\"" + command + "\",\"data\":\"" + data + "\"}";
        assertEquals(expectedSendString, packet.toSendString());
    }

    @Test
    public void testStringifiedPacketConstruction() throws IOException, Packet.InvalidPacketConstructionException {
        String rawStringifiedPacket = "{\"namespace\":\"/group1/team1/\",\"command\":\"move\",\"data\":\"{}\"}";

        Packet.PacketBuilder builder = new Packet.PacketBuilder(rawStringifiedPacket);
        Packet packet = builder.build();

        assertArrayEquals("/group1/team1/".split("/"), packet.namespacePath);
        assertEquals("move", packet.command);
        assertEquals(null, packet.clientSessionId);
        assertEquals(new ObjectMapper().createObjectNode(), packet.data);
    }

    @Test
    public void testValidPacketConstructionWithData() throws IOException, Packet.InvalidPacketConstructionException {
        String namespace = UUID.randomUUID().toString();
        String command = "create pente lobby";
        UUID clientSessionId = UUID.randomUUID();
        String namespaceName = "test namespace name";
        String rawStringifiedPacket = "{\"namespace\":\"/" + namespace + "/\",\"command\":\"" + command + "\",\"data\":{\"lobbyName\":\"" + namespaceName + "\"}}";

        Packet.PacketBuilder builder = new Packet.PacketBuilder(rawStringifiedPacket)
            .setClientSessionId(clientSessionId);

        Packet createPenteLobby = builder.build();

        assertArrayEquals(("/" + namespace + "/").split("/"), createPenteLobby.namespacePath);
        assertEquals(command, createPenteLobby.command);
        assertEquals(clientSessionId, createPenteLobby.clientSessionId);
        // assertEquals(new ObjectMapper().createObjectNode(), createPenteLobby.data);
    }

    @Test
    public void testEmptyNamespaceStringification() throws Packet.InvalidPacketConstructionException {
        String namespace = "";
        String command = "join";
        UUID clientSessionId = UUID.randomUUID();
        JsonNode data = new ObjectMapper().createObjectNode();

        Packet.PacketBuilder builder = new Packet.PacketBuilder(namespace, command)
                .setClientSessionId(clientSessionId)
                .setData(data);

        Packet packet = builder.build();

        String expectedSendString = "{\"namespace\":\"/\",\"command\":\"" + command + "\",\"data\":\"" + data + "\"}";
        assertEquals(expectedSendString, packet.toSendString());
    }

    @Test
    public void testNullDataStringification() throws Packet.InvalidPacketConstructionException {
        String namespace = "/lobby/";
        String command = "chat";
        UUID clientSessionId = UUID.randomUUID();

        Packet.PacketBuilder builder = new Packet.PacketBuilder(namespace, command)
                .setClientSessionId(clientSessionId);

        Packet packet = builder.build();

        String expectedSendString = "{\"namespace\":\"" + namespace + "\",\"command\":\"" + command + "\",\"data\":\"null\"}";
        assertEquals(expectedSendString, packet.toSendString());
    }

    @Test
    public void testToStringMethod() throws Packet.InvalidPacketConstructionException {
        String namespace = "/game/";
        String command = "score";
        UUID clientSessionId = UUID.randomUUID();
        JsonNode data = new ObjectMapper().createObjectNode();

        Packet.PacketBuilder builder = new Packet.PacketBuilder(namespace, command)
                .setClientSessionId(clientSessionId)
                .setData(data);

        Packet packet = builder.build();

        String expectedToString = "Message: {\n/game/score\n" + "data: " + data.toString() + "\n}";
        assertEquals(expectedToString, packet.toPrettyPrintString());
    }

    @Test(expected = Packet.InvalidPacketConstructionException.class)
    public void testStringifiedPacketConstructionWithMissingNamespace() throws Packet.InvalidPacketConstructionException, IOException {
        // Missing the "namespace" attribute intentionally
        String rawStringifiedPacket = "{\"command\":\"jump\",\"data\":\"{}\"}";

        new Packet.PacketBuilder(rawStringifiedPacket).build();
    }
}