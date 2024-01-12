package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.*;
import java.io.*;

public abstract class AbstractNamespace {
    protected final UUID namespaceId;
    protected final String name;


    protected Map<UUID, ClientProxy> sessionIdToClientProxyMap = new HashMap<>();


    protected AbstractNamespace parentNamespace;

    // all subset namespaces of the current namespace 
    //      e.g. a lobby has teams
    protected List<AbstractNamespace> childrenNamespaces;


    public AbstractNamespace(UUID namespaceId, String name, AbstractNamespace parentNamespace) {
        this.name = name;

        this.childrenNamespaces = new ArrayList<>();
        this.namespaceId = namespaceId;
    }

    public abstract Packet handlePacket(Packet p);
    public abstract void connectClient(UUID sessionId, ClientProxy clientInfo);
    public abstract void disconnectClient(UUID sessionId);


    /**
     * Used to create a new unique id among the current namespaces child namespaces 
     * @return
     */
    protected UUID rollNewChildNamespaceId() {
        UUID uniqueNamespaceId = UUID.randomUUID();

        Map<UUID, Integer> childrenUUIDs = new HashMap<>();

        for (int i = 0; i < this.childrenNamespaces.size(); i++) {  
            childrenUUIDs.put(this.childrenNamespaces.get(i).namespaceId, null);
        }

        while(childrenUUIDs.containsKey(uniqueNamespaceId)) {
            uniqueNamespaceId = UUID.randomUUID();
        }

        return uniqueNamespaceId;
    }

    /**
     * Checks if an incoming packet comes from a client that is in the namespace.
     * @param p
     * @return
     */
    protected boolean isPacketFromClientInNamespace(Packet p) {
        UUID sessionId = p.clientSessionId;

        if(sessionId == null) {
            return false;
        } 

        if(this.sessionIdToClientProxyMap.containsKey(sessionId)) {
            return true;
        }

        return false;
    }

    
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
     * Gets the number of sessions/clients active within a given namespace tree.
     * Useful for determining if a game lobby is empty regardless of team structures/client subsets created. 
     * @return
     */
    public int getNumClientsInNamespaceTree() {
        // leaf node base case
        if (this.childrenNamespaces.size() == 0) { 
            return this.sessionIdToClientProxyMap.size();
        } 

        int activeChildrenNamespaceSessions = 0;

        for (AbstractNamespace abstractNamespace : childrenNamespaces) {
            activeChildrenNamespaceSessions += abstractNamespace.getNumClientsInNamespaceTree();
        }

        return this.sessionIdToClientProxyMap.size() + activeChildrenNamespaceSessions;
    }

    public UUID getNamespaceId() {
        return namespaceId;
    }

    public String getName() {
        return name;
    }

    public AbstractNamespace getParentNamespace() {
        return parentNamespace;
    }

    public List<AbstractNamespace> getChildrenNamespaces() {
        return childrenNamespaces;
    }

    public Map<UUID, ClientProxy> getSessionIdToClientProxyMap() {
        return sessionIdToClientProxyMap;
    }
}
