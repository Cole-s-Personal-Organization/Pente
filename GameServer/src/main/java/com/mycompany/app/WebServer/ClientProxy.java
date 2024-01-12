package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.*;

/**
 * A server-side representation of a client that is associated with the server.
 * 
 * @author Cole 
 * @version 1.0.0
 */
public class ClientProxy {
    private UUID sessionId;
    private String name;
    private UUID id; 
    private ReplicationManagerService replicationManagerService;

    public ClientProxy(UUID id, UUID sessionId, String name) {
        this.sessionId = sessionId;
        this.name = name;
        this.id = id;
    }

    public void setReplicationManagerService(ReplicationManagerService replicationManagerService) {
        this.replicationManagerService = replicationManagerService;
    }

    /**
     * returns a json representation of this client proxy object
     * @return Json client object
     */
    public JsonNode toJson() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();

        node.put("clientName", name);
        node.put("clientId", id.toString());
        // node.put("sessionId", sockInetAddress.toString());

        return (JsonNode)node;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public ReplicationManagerService getReplicationManagerService() {
        return replicationManagerService;
    }
}
