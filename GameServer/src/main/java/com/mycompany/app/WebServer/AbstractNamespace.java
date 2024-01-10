package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.*;
import java.io.*;

public abstract class AbstractNamespace {
    public final UUID id;
    public final String name;


    public Map<UUID, ClientProxy> sessionIdToClientProxyMap = new HashMap<>();


    protected AbstractNamespace parentNamespace;

    // all subset namespaces of the current namespace 
    //      e.g. a lobby has teams
    protected AbstractNamespace[] childrenNamespaces;


    public AbstractNamespace(AbstractNamespace parentNamespace, String name, UUID namespaceUuid) {
        this.name = name;

        // each sibling namespace will have a unique id to differentiate itself from the others
        this.id = namespaceUuid;
    }

    public abstract Packet handlePacket(Packet p);
    public abstract void connectClient(UUID sessionId, ClientProxy clientInfo);
    public abstract void disconnectClient(UUID sessionId);


    
    public JsonNode[] getSessionsInNamespace() {
        List<JsonNode> jsonSessions = new ArrayList<>();

        for (Map.Entry<UUID, ClientProxy> entry : this.sessionIdToClientProxyMap.entrySet()) {
            ObjectNode sessionNode = JsonNodeFactory.instance.objectNode();
            
            UUID sessionId = entry.getKey();
            ClientProxy clientObj = entry.getValue();

            JsonNode clientInfoNode = clientObj.toJson();

            sessionNode.put("sessionId", sessionId.toString());
            sessionNode.set("clientInfo", clientInfoNode);

            jsonSessions.add((JsonNode)sessionNode);
        }

        return (JsonNode[])jsonSessions.toArray();
    }

    /**
     * A helper function for meant to standardize welcome packets
     * @param namespace
     * @return
     */
    protected Packet createWelcomePacket(String namespace) {
        Packet welcomePacket = null;

        try {
            welcomePacket = new Packet.PacketBuilder(namespace, "GRRREEETINGS").build();
        } catch (Packet.InvalidPacketConstructionException e) {
            
        }

        return welcomePacket;
    } 
}
