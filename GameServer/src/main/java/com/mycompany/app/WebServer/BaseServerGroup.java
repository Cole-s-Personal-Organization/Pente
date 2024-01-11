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
    public Packet handlePacket(Packet p) {
        Packet responsePacket = null;

        return responsePacket;
    }
}
