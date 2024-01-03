package com.mycompany.app.WebServer;

import java.net.Socket;
import java.util.*;
import java.io.*;

import com.mycompany.app.WebServer.MyWebServer.ClientInstance;

public class Namespace {
    public final UUID uuid;
    public final String name;


    // users will always be placed in the leaf node namespaces
    // their actions will start in the leaf and be handled there, 
    //      if there is no overriding implementation of the action, continue to parent until you find one
    private Map<UUID, ClientInstance> clientIdToInstanceMap = new HashMap<>();

    // all subset namespaces of the current namespace 
    //      e.g. a lobby has teams
    private Namespace[] childrenNamespaces;

    // a controller for the namespace, each namespace will have one, 
    //      it will dictate how commands from incoming messages are handled
    public ControllerInterface controller;


    public ProtocolInterface protocol;



    public Namespace(String name, UUID[] otherNamespaceUuids) {
        this.name = name;

        // ROLL NEW IDS UNTIL UNIQUENESS IS ACHEIVED
        UUID newNamespaceId = UUID.randomUUID();
        List<UUID> otherNamespaceUuidsListObj = Arrays.asList(otherNamespaceUuids);

        // continue making new namespace ids until I get a unique one
        while (otherNamespaceUuidsListObj.contains(newNamespaceId)) {
            newNamespaceId = UUID.randomUUID();
        }

        this.uuid = newNamespaceId;

        this.controller = new BaseServerController();
    }


    public void connectClient(UUID clientId, ClientInstance client) {

        this.clientIdToInstanceMap.put(clientId, client);
    }

    public void disconnectClient(UUID clientId) {
        this.clientIdToInstanceMap.remove(clientId);
    }

    /**
     * Send message to client within namespace
     * @param message
     * @param clientId
     */
    public void sendMessage(Message message, UUID clientId) {
        Socket socket = this.clientIdToInstanceMap.get(clientId).clientSocket;

        try {
            OutputStream outputStream = socket.getOutputStream();

            outputStream.write(message.toSendString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send message to all clients in namespace
     * @param message
     */
    public void broadcastMessage(Message message) {
        List<ClientInstance> clients = new ArrayList<>(this.clientIdToInstanceMap.values());

        try {
            for (ClientInstance clientInstance : clients) {
                OutputStream outputStream = clientInstance.clientSocket.getOutputStream();

                outputStream.write(message.toSendString().getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setController(ControllerInterface controller) {
        this.controller = controller;
    }

    
}
