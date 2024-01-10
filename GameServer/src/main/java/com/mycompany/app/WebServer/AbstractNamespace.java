package com.mycompany.app.WebServer;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.*;
import java.io.*;

public abstract class AbstractNamespace {
    public final UUID id;
    public final String name;


    public Map<UUID, ClientProxy> sessionIdToClientProxyMap = new HashMap<>();

    // all subset namespaces of the current namespace 
    //      e.g. a lobby has teams
    protected AbstractNamespace[] childrenNamespaces;


    public AbstractNamespace(String name, UUID namespaceUuid) {
        this.name = name;

        // each sibling namespace will have a unique id to differentiate itself from the others
        this.id = namespaceUuid;
    }

    public abstract void handlePacket(Packet p, AbstractNamespace n);
    public abstract void connectClient(UUID sessionId, ClientProxy clientInfo);
    public abstract void disconnectClient(UUID sessionId);


    /**
     * Recurrsivley routes through namespace tree to find the namespace which the message is going to be sent to.
     * @param currNamespace
     * @param currNamespaceRouteLst
     * @param message
     */
    // public void routeCommand(BaseNamespace currNamespace, String[] currNamespaceRouteLst, Packet message) {
    //     if (currNamespaceRouteLst.length > 0) {
    //         // each namespace string within the list is an id which maps to a child namespace if one exists
    //         String nextNamespace = currNamespaceRouteLst[0];
    //         UUID nextNamespaceId = UUID.fromString(nextNamespace);
    //         int indexOfChild = getIndexOfChildNamespaceById(nextNamespaceId);

    //         // pop the first element in the list
    //         String[] shortenedNamespaceRouteLst = Arrays.copyOfRange(currNamespaceRouteLst, 1, currNamespaceRouteLst.length);

    //         routeCommand(childrenNamespaces[indexOfChild], shortenedNamespaceRouteLst, message);
    //     } else {
    //         // we have reached the messages intended namespace

    //         this.controller.handleCommand(currNamespace, message);
    //     }
    // }

    private UUID[] getChildrenNamespaceIds() {
        UUID[] childrenIdList = new UUID[this.childrenNamespaces.length];

        for (int i = 0; i < childrenNamespaces.length; i++) {
            childrenIdList[i] = childrenNamespaces[i].id;
        }
        return childrenIdList;
    }


    /**
     * Iterates through current namespaces children looking for one that has an id which matches the parameters.
     * @param id
     * @return
     * @throws NoSuchElementException
     */
    private int getIndexOfChildNamespaceById(UUID id) throws NoSuchElementException {
        for (int i = 0; i < childrenNamespaces.length; i++) {
            if(childrenNamespaces[i].id == id) {
                return i;
            } 
        }

        throw new NoSuchElementException("Searched id does not exist within the child namespace list.");
    }

    /**
     * Send message to client within namespace
     * @param message
     * @param clientId
     */
    public void sendMessage(Packet message, UUID clientId) {
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
    public void broadcastMessage(Packet message) {
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

    
    public JsonNode[] getSessionsInNamespace() {
        List<JsonNode> jsonSessions = new ArrayList<>();

        for (Map.Entry<UUID, ClientProxy> entry : this.sessionIdToClientProxyMap.entrySet()) {
            ObjectNode sessionNode = JsonNodeFactory.instance.objectNode();
            
            UUID sessionId = entry.getKey();
            ClientProxy clientObj = entry.getValue();

            JsonNode clientInfoNode = clientObj.toJson();

            sessionNode.put("sessionId", sessionId.toString());
            sessionNode.set("clientInfo", clientInfoNode);

            jsonSessions.add((JsonNode)sessionNode);
        }

        return (JsonNode[])jsonSessions.toArray();
    }
}
