package com.mycompany.app.WebServer;

import java.util.*;
import java.net.*;

/**
 * Tracks the state of each client.
 * Contains a separate repliction manager for each client, 
 */
public class ClientProxy {
    private InetAddress sockInetAddress;
    private String name;
    private UUID playerId; 
    private ReplicationManagerService replicationManagerService;

    public ClientProxy(InetAddress socketAddress, String name, UUID playerId) {
        this.sockInetAddress = socketAddress;
        this.name = name;
        this.playerId = playerId;
    }

    public void setReplicationManagerService(ReplicationManagerService replicationManagerService) {
        this.replicationManagerService = replicationManagerService;
    }
}
