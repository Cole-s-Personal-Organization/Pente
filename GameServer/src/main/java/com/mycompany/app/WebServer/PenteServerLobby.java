package com.mycompany.app.WebServer;

import java.io.*;
import java.net.*;
import java.util.*;

import com.mycompany.app.Game.Pente.PenteGameController;

public class PenteServerLobby extends AbstractNamespace {
    
    PenteGameController controller;

    public PenteServerLobby(UUID namespaceId, String name, AbstractNamespace parentNamespace) {
        super(namespaceId, name, parentNamespace);
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