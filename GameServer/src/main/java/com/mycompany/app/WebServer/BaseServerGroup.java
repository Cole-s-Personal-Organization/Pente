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

    /**
     * Note parent namespace for BaseServerGroup should always be null.
     * @param parentNamespace
     * @param name
     * @param id
     */
    public BaseServerGroup(AbstractNamespace parentNamespace, String name, UUID id) {
        super(parentNamespace, name, id);
        
        this.activateInetAddressToSessionIds = new HashMap<>();
    }

    @Override
    public void disconnectClient(UUID clientSessionId) {
        // this.activateInetAddressToSessionIds.
        
    }

    @Override
    public void connectClient(UUID sessionId, ClientProxy clientInfo) {
        // TODO Auto-generated method stub
        
    }

    private boolean isPacketSenderConnected(Packet p) {
        return this.activateInetAddressToSessionIds.containsKey(p.senderSocketAddress);
    }

    @Override
    public void handlePacket(Packet p) {
        // route packet to command handlers based on session status
        if (!isPacketSenderConnected(p)) {
            handleMessageFrom_Unauthorized(p);
        } else {
            handleMessageFrom_Authorized(p);
        }
    }

    public boolean isClientConnectedToServer(InetAddress address) {
        return (this.activateInetAddressToSessionIds.containsKey(address));
    } 

    /**
     * Handles a packet sent from an authorized sender that already has a session.
     * @param p
     * @param n
     */
    private void handleMessageFrom_Authorized(Packet p) {
        UUID senderSessionId = this.activateInetAddressToSessionIds.get(p.senderSocketAddress);
        if (senderSessionId == null) { 
            return;
        } 

        switch (p.command) {
            case "":
                
                break;
        
            default:
                break;
        }
    }

    private void handleMessageFrom_Unauthorized(Packet p) {
        JsonNode payload = p.data;
        String clientName = payload.get("clientName").asText();
        
        switch (p.command) {
            case "GRREEETINGS":
                
                UUID clientId = getClientID();

                // create clientProxy
                ClientProxy proxy = new ClientProxy(p.senderSocketAddress, clientName, clientId);


                // enter client into base level namespace
                UUID sessionId = this.rollNewUniqueSessionID();
                this.activateInetAddressToSessionIds.put(p.senderSocketAddress, sessionId);
                this.connectClient(sessionId, proxy);

                // welcome client - use connected base controller to send welcome message
                Packet welcomePacket = this.createWelcomePacket(clientName);
                this.sendMessage(welcomePacket, sessionId);


                // init clientProxy replication manager
                ReplicationManagerService rManagerService = new ReplicationManagerService();
                proxy.setReplicationManagerService(rManagerService);

                this.sessionIdToClientProxyMap.put(sessionId, proxy);
                break;

            default:
                System.out.println("Bad incoming packet from unknown client at socket " + p.senderSocketAddress.toString());
                break;
        }
    }

    private UUID rollNewUniqueSessionID() {
        UUID sessionId = UUID.randomUUID();
        Collection<UUID> takenSessionIds = this.activateInetAddressToSessionIds.values();

        while(takenSessionIds.contains(sessionId)) {
            sessionId = UUID.randomUUID();
        }

        return sessionId;
    }

    private UUID getClientID() {
        // for now just set to a random id, needs to change later if we plan to make users
        return UUID.randomUUID();
    }
}
