package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.*;
import java.io.*;


/**
 * Represents the abstract grouping of client connections to MyWebServer. 
 * Provides functionality to manage those clients and handle their incoming and outgoing packets.
 * 
 * @author Cole, Dan 
 * @version 1.0.0
 */
public abstract class AbstractNamespace {
    /**
     * The id used to uniquely identify this namespace among its peers.
     */
    protected final UUID namespaceId;

    /**
     * The string name of the lobby. 
     */
    protected final String name;

    /**
     * Hashmap containing mappings of client session id's to their server proxy objects
     * @see ClientProxy
     */
    protected Map<UUID, ClientProxy> sessionIdToClientProxyMap = new HashMap<>();

    /**
     * The namespace that is parent to the current namespace.
     * The parent namespace's client is a superset to the current namespace.
     */
    protected AbstractNamespace parentNamespace;    

    /**
     * A List of all child namespace's relative to the current namespace.
     * Each namespace is a subset of clients relative to its parent.
     *      e.g. a game lobby can have teams
     */
    protected List<AbstractNamespace> childrenNamespaces;

    /**
     * Constructor
     * @param namespaceId A namespace UUID object
     * @param name A string lobby name
     * @param parentNamespace The parent AbstractNamespace of this namespace
     */
    public AbstractNamespace(UUID namespaceId, String name, AbstractNamespace parentNamespace) {
        this.name = name;

        this.childrenNamespaces = new ArrayList<>();
        this.namespaceId = namespaceId;
    }

    /**
     * Handles an incoming packet according to the extended implementation of this function.
     * @param p a packet
     * @return a response packet
     */
    public abstract Packet handlePacket(Packet p);

    /**
     * Connects the client to this namespace, handles any entrance commands associated with joining said namespace.
     * @param sessionId A client's session UUID object
     * @param clientInfo A client's ClientProxy object
     */
    public abstract void connectClient(UUID sessionId, ClientProxy clientInfo);

    /**
     * Disconnects the client from this namespace, handles any cleanup required for the extended namespace.
     * @param sessionId a client's session UUID object 
     */
    public abstract void disconnectClient(UUID sessionId);


    /**
     * Used to create a new unique id among the current namespaces child namespaces 
     * @return Random UUID unique among children
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
     * @param p A packet
     * @return boolean representing if the client who sent the packet origininates from this namespace.
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

    
    // public JsonNode[] getSessionsInNamespace() {
    //     List<JsonNode> jsonSessions = new ArrayList<>();

    //     for (Map.Entry<UUID, ClientProxy> entry : this.sessionIdToClientProxyMap.entrySet()) {
    //         ObjectNode sessionNode = JsonNodeFactory.instance.objectNode();
            
    //         UUID sessionId = entry.getKey();
    //         ClientProxy clientObj = entry.getValue();

    //         JsonNode clientInfoNode = clientObj.toJson();

    //         sessionNode.put("sessionId", sessionId.toString());
    //         sessionNode.set("clientInfo", clientInfoNode);

    //         jsonSessions.add((JsonNode)sessionNode);
    //     }

    //     return (JsonNode[])jsonSessions.toArray();
    // }

    /**
     * Gets the number of sessions/clients active within a given namespace tree.
     * Useful for determining if a game lobby is empty regardless of team structures/client subsets created. 
     * @return Number of clients in root node's tree of namespaces.
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
