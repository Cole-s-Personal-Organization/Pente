package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.mycompany.app.Game.Pente.PenteGameController;

import java.net.*;
import java.io.*;

public class BaseServerGroup extends AbstractNamespace {

    public BaseServerGroup(String name, UUID id) {
        super(name, id);
        
    }

    @Override
    public void disconnectClient(UUID clientSessionId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectClient(UUID clientSessionId) {
        this.
    }

    @Override
    public void handlePacket(Packet p, AbstractNamespace n) {
        if (p.getSenderSessionId() != null) {
            handleMessageFrom_Authorized(p, n);
        } else {
            handleMessageFrom_Unauthorized(p, n);
        }
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
        switch (p.getCommand()) {
            case "GRREEETINGS":
                JsonNode payload = p.getData();
                String clientName = payload.get("clientName").asText();
                InetAddress ipAddress = InetAddress.getByName(payload.get("ipAddress").asText());
                UUID clientId = UUID.randomUUID();

                // create clientProxy
                ClientProxy proxy = new ClientProxy(ipAddress, clientName, clientId);


                // enter client into base level namespace
                this.baseNamespace.connectClient(ipAddress);

                // welcome client - use connected base controller to send welcome message
                this.baseNamespace.controller.welcomeNewClient();


                // init clientProxy replication manager
                ReplicationManagerService rManagerService = new ReplicationManagerService();
                proxy.setReplicationManagerService(rManagerService);

                this.addressToClientProxyMap.put(fromAddress, proxy);
                break;

            default:
                System.out.println("Bad incoming packet from unknown client at socket " + fromAddress.toString());
                break;
        }
    }
}
