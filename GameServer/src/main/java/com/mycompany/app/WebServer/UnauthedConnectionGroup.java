package com.mycompany.app.WebServer;

import java.io.*;
import java.net.*;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;

public class UnauthedConnectionGroup extends AbstractNamespace {

    UnauthedConnectionGroup(String name, UUID id) {
        super(name, id);
    }
    
    @Override
    public void handlePacket(Packet p, AbstractNamespace n) {
        switch (p.getCommand()) {
            case "GRREEETINGS":
                JsonNode payload = p.getData();
                String clientName = payload.get("clientName").asText();
                InetAddress ipAddress = InetAddress.getByName(payload.get("ipAddress").asText());
                UUID clientId = UUID.randomUUID();

                // create clientProxy
                ClientProxy proxy = new ClientProxy(fromAddress, clientName, clientId);


                // enter client into base level namespace
                this.baseNamespace.connectClient(fromAddress);

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

    @Override
    public void connectClient(UUID clientId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disconnectClient(UUID clientId) {
        // TODO Auto-generated method stub
        
    }

    
}
