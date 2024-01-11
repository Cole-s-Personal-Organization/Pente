package com.mycompany.app.Game.Pente;

import java.io.*;
import java.net.*;
import java.util.*;

import com.mycompany.app.WebServer.AbstractNamespace;
import com.mycompany.app.WebServer.ClientProxy;
import com.mycompany.app.WebServer.Packet;

public class PenteServerLobby extends AbstractNamespace {
    
    PenteGameController controller;

    public PenteServerLobby(AbstractNamespace namespace, String name, UUID id) {
        super(namespace, name, id);
        this.controller = new PenteGameController();
    }

    @Override
    public void connectClient(UUID sessionId, ClientProxy clientInfo) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disconnectClient(UUID clientId) {
        // TODO Auto-generated method stub
    }

    @Override
    public Packet handlePacket(Packet p) {
        // TODO Auto-generated method stub
        return null;
    }
}
