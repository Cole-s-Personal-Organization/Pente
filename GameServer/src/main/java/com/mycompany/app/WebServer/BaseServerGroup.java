package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.mycompany.app.Game.Pente.PenteGameController;
import com.mycompany.app.WebServer.Packet.PacketBuilder;

import java.net.*;
import java.io.*;

public class BaseServerGroup extends AbstractNamespace {

    /**
     * Note parent namespace for BaseServerGroup should always be null.
     * @param parentNamespace
     * @param name
     */
    public BaseServerGroup(UUID namespaceId, String name, AbstractNamespace parentNamespace) {
        super(namespaceId, name, parentNamespace);
    }

    @Override
    public void disconnectClient(UUID clientSessionId) {
        this.sessionIdToClientProxyMap.remove(clientSessionId);
    }

    @Override
    public void connectClient(UUID sessionId, ClientProxy clientInfo) {
        this.sessionIdToClientProxyMap.put(sessionId, clientInfo);
    }

    @Override
    public Packet handlePacket(Packet p) {
        Packet responsePacket = null;

        System.out.println("Start packet handling process");

        // filter packets that aren't allowed to comunicate with this namespace due to bad internal clientSessionId
        if(!isPacketFromClientInNamespace(p)) {
            return PacketHelpers.createErrorPacket(p, "Packet sent to namespace user wasn't a part of.");
        }

        System.out.println("packet verified a part of namespace.");

        switch (p.command) {

            case "create pente lobby":
                System.out.println("creating pente lobby");
                UUID lobbyNamespaceId = this.rollNewChildNamespaceId();
                String lobbyName = p.data.get("lobbyName").asText();

                PenteServerLobby lobby = new PenteServerLobby(lobbyNamespaceId, lobbyName, parentNamespace);
                // game creator credentials
                UUID clientSessionId = p.clientSessionId;
                ClientProxy clientProxy = this.sessionIdToClientProxyMap.get(clientSessionId);

                this.childrenNamespaces.add(lobby);

                this.disconnectClient(clientSessionId);
                lobby.connectClient(clientSessionId, clientProxy);
                break;

            case "message":
                break;
        
            default:
                break;
        }

        return responsePacket;
    }
}
