package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.mycompany.app.Game.Pente.PenteGameModel;
import com.mycompany.app.WebServer.Packet.PacketBuilder;

import java.net.*;
import java.io.*;


/**
 * Represents the base server in which all clients who initially connect, join.
 * Extension of Abstract Namespace.
 * 
 * @author Cole, Dan 
 * @version 1.0.0
 */
public class BaseServerGroup extends AbstractNamespace {

    /**
     * Constructor
     * Note parent namespace for BaseServerGroup should always be null.
     * @param namespaceId A UUID referencing the namespace.
     * @param parentNamespace The parent AbstractNamespace to this namespace.
     * @param name A string.
     */
    public BaseServerGroup(UUID namespaceId, String name, AbstractNamespace parentNamespace) {
        super(namespaceId, name, parentNamespace);
    }

    /**
     * {@inheritDoc}
     * 
     * Overridden method to disconnect a client from the base namespace group.
     * @param sessionId A client's session UUID.
     */
    @Override
    public void disconnectClient(UUID sessionId) {
        this.sessionIdToClientProxyMap.remove(sessionId);
    }

    /**
     * {@inheritDoc}
     * 
     * Overridden method to connect a client to the base namespace group.
     * @param sessionId A client's session UUID object
     * @param clientInfo A client's ClientProxy object
     */
    @Override
    public void connectClient(UUID sessionId, ClientProxy clientInfo) {
        this.sessionIdToClientProxyMap.put(sessionId, clientInfo);
    }

    /**
     * {@inheritDoc}
     * 
     * Overridden method to handle a packet created by a client within the namespace according to the base server group's command list.
     * @param p A packet.
     */
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
