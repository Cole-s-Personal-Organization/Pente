package com.mycompany.app;

import static org.junit.Assert.*;

import java.util.*;
import java.io.IOException;
import java.net.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.app.WebServer.AbstractNamespace;
import com.mycompany.app.WebServer.BaseServerGroup;
import com.mycompany.app.WebServer.ClientProxy;
import com.mycompany.app.WebServer.PenteServerLobby;

public class BaseServerGroupNamespaceTest {

    // private ClientProxy createBobTheClient() {
    //     UUID sessionId = UUID.randomUUID();
    //     UUID clientId = UUID.randomUUID();
    //     ClientProxy clientProxy = new ClientProxy(clientId, sessionId, "bob");

    //     return clientProxy;
    // }
    
    // @Test 
    // public void testBaseServerGroupConstruction() {
    //     BaseServerGroup baseGroup = new BaseServerGroup(UUID.randomUUID(), "base", null);

    //     assertNotNull(baseGroup);
    //     assertEquals("base", baseGroup.getName());
    //     assertNotNull(baseGroup.getNamespaceId());
    //     assertEquals(0, baseGroup.getChildrenNamespaces().size());
    //     assertNotNull(baseGroup.getSessionIdToClientProxyMap());
    //     assertEquals(baseGroup.getSessionIdToClientProxyMap().size(), 0);
    // }

    // @Test
    // public void addClientToBaseNamespaceTest() {
    //     BaseServerGroup baseGroup = new BaseServerGroup(UUID.randomUUID(), "base", null);

    //     // sample client
    //     ClientProxy bobClientProxy = createBobTheClient();

    //     baseGroup.connectClient(bobClientProxy.getSessionId(), bobClientProxy);

    //     assertNotNull(baseGroup.getSessionIdToClientProxyMap());
    //     assertEquals(baseGroup.getSessionIdToClientProxyMap().size(), 1);

    //     Map<UUID, ClientProxy> sessionToClientMap = baseGroup.getSessionIdToClientProxyMap();
    //     ClientProxy storedClientProxy = sessionToClientMap.get(bobClientProxy.getSessionId()); 
    //     assertEquals(storedClientProxy, bobClientProxy);
    // }

    // @Test
    // public void removeClientFromBaseNamespaceTest() {
    //     BaseServerGroup baseGroup = new BaseServerGroup(UUID.randomUUID(), "base", null);

    //     // sample client
    //     ClientProxy bobClientProxy = createBobTheClient();

    //     baseGroup.connectClient(bobClientProxy.getSessionId(), bobClientProxy);

    //     baseGroup.disconnectClient(bobClientProxy.getSessionId());

    //     assertNotNull(baseGroup.getSessionIdToClientProxyMap());
    //     assertEquals(baseGroup.getSessionIdToClientProxyMap().size(), 0);
    // }

    // @Test 
    // public void addPenteLobbyToBaseNamespaceTest() throws IOException, Packet.InvalidPacketConstructionException {
    //     UUID baseNamespaceId = UUID.randomUUID();
    //     BaseServerGroup baseGroup = new BaseServerGroup(UUID.randomUUID(), "base", null);

    //     // sample namespace
    //     String namespaceName = "Bob's Game";

    //     // sample client
    //     ClientProxy bobClientProxy = createBobTheClient();

    //     baseGroup.connectClient(bobClientProxy.getSessionId(), bobClientProxy);

    //     // create pente lobby packet
    //     String namespace = baseNamespaceId.toString();
    //     String command = "create pente lobby";
    //     String rawStringifiedPacket = "{\"namespace\":\"/" + namespace + "/\",\"command\":\"" + command + "\",\"data\":{\"lobbyName\":\"" + namespaceName + "\"}}";

    //     Packet.PacketBuilder builder = new Packet.PacketBuilder(rawStringifiedPacket)
    //         .setClientSessionId(bobClientProxy.getSessionId());

    //     Packet createPenteLobby = builder.build();

    //     baseGroup.handlePacket(createPenteLobby);

    //     assertEquals(1, baseGroup.getChildrenNamespaces().size());

    //     PenteServerLobby builtLobby = (PenteServerLobby)baseGroup.getChildrenNamespaces().get(0);
    //     assertNotNull(builtLobby);
    //     assertEquals(namespaceName, builtLobby.getName());
    // }
}
