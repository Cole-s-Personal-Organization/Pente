package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.mycompany.app.Game.Pente.PenteGameController;

import java.net.*;
import java.io.*;

public class BaseServerGroup extends AbstractNamespace {

    /**
     * Note parent namespace for BaseServerGroup should always be null.
     * @param parentNamespace
     * @param name
     * @param id
     */
    public BaseServerGroup(AbstractNamespace parentNamespace, String name, UUID id) {
        super(parentNamespace, name, id);
    }

    @Override
    public void disconnectClient(UUID clientSessionId) {
        // this.activateInetAddressToSessionIds.
        
    }

    @Override
    public void connectClient(UUID sessionId, ClientProxy clientInfo) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Packet handlePacket(Packet p) {
        Packet responsePacket = null;

        return responsePacket;
    }
}
