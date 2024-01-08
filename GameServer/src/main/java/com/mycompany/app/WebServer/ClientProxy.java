package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.*;

/**
 * Tracks the state of each client.
 * Contains a separate repliction manager for each client, 
 */
public class ClientProxy {
    private InetAddress sockInetAddress;
    private String name;
    private UUID id; 
    private ReplicationManagerService replicationManagerService;

    public ClientProxy(InetAddress socketAddress, String name, UUID id) {
        this.sockInetAddress = socketAddress;
        this.name = name;
        this.id = id;
    }

    public void setReplicationManagerService(ReplicationManagerService replicationManagerService) {
        this.replicationManagerService = replicationManagerService;
    }

    
    public JsonNode toJson() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();

        node.put("clientName", name);
        node.put("clientId", id.toString());
        node.put("ipAddress", sockInetAddress.toString());

        return (JsonNode)node;
    }
}
