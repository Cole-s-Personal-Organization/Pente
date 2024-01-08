package com.mycompany.app.Game.Pente;

import java.io.*;
import java.net.*;
import java.util.*;

import com.mycompany.app.WebServer.AbstractNamespace;
import com.mycompany.app.WebServer.Packet;

public class PenteServerLobby extends AbstractNamespace {
    
    PenteGameController controller;

    public PenteServerLobby(String name, UUID id) {
        super(name, id);
        this.controller = new PenteGameController();
    }

    @Override
    public void disconnectClient(UUID clientId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectClient(UUID clientId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handlePacket(Packet p, AbstractNamespace n) {
        // TODO Auto-generated method stub
        
    }
}
