package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.mycompany.app.Game.Pente.PenteGameController;

import java.net.*;
import java.io.*;

public class BaseServerGroup extends AbstractNamespace {

    // because this is the base namespace, all users will be entered in here on connection
    //      we can keep active sessionId's for all namespaces here to prevent id clashes
    private Map<InetAddress, UUID> activateInetAddressToSessionIds;

    public BaseServerGroup(String name, UUID id) {
        super(name, id);
        
        this.activateInetAddressToSessionIds = new HashMap<>();
    }

    @Override
    public void disconnectClient(UUID clientSessionId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectClient(UUID sessionId, ClientProxy clientInfo) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handlePacket(Packet p, AbstractNamespace n) {
        if (p.getSenderSessionId() != null) {
            handleMessageFrom_Authorized(p, n);
        } else {
            handleMessageFrom_Unauthorized(p, n);
        }
    }

    public boolean isClientConnectedToServer(InetAddress address) {
        return (this.activateInetAddressToSessionIds.containsKey(address));
    } 

    /**
     * 
     * @param p
     * @param n
     */
    private void handleMessageFrom_Authorized(Packet p, AbstractNamespace n) {
        UUID senderSessionId = p.getSenderSessionId();
        if (senderSessionId == null) { 
            return;
        } 

        switch (p.getCommand()) {
            case "GRREEETINGS": // welcome command to the current namespace
                connectClient(senderSessionId);
                break;
        
            default:
                break;
        }
    }

    private void handleMessageFrom_Unauthorized(Packet p, AbstractNamespace n) {
        JsonNode payload = p.getData();
        String clientName = payload.get("clientName").asText();
        InetAddress ipAddress = InetAddress.getByName(payload.get("ipAddress").asText());
        
        switch (p.getCommand()) {
            case "GRREEETINGS":
                UUID clientId = UUID.randomUUID();

                // create clientProxy
                ClientProxy proxy = new ClientProxy(ipAddress, clientName, clientId);


                // enter client into base level namespace
                UUID sessionId = this.rollNewUniqueSessionID();
                this.activateSessionIds.add(sessionId);
                this.connectClient(sessionId, proxy);

                // welcome client - use connected base controller to send welcome message
                Packet welcomePacket = null;
                this.sendMessage(welcomePacket, sessionId);


                // init clientProxy replication manager
                ReplicationManagerService rManagerService = new ReplicationManagerService();
                proxy.setReplicationManagerService(rManagerService);

                this.sessionIdToClientProxyMap.put(sessionId, proxy);
                break;

            default:
                System.out.println("Bad incoming packet from unknown client at socket " + ipAddress.toString());
                break;
        }
    }

    private UUID rollNewUniqueSessionID() {
        UUID sessionId = UUID.randomUUID();

        while(this.activateSessionIds.contains(sessionId)) {
            sessionId = UUID.randomUUID();
        }

        return sessionId;
    }
}
