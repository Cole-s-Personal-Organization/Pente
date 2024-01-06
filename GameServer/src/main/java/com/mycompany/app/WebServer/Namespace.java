package com.mycompany.app.WebServer;

import java.net.Socket;
import java.util.*;
import java.net.*;
import java.io.*;

public class Namespace {
    public final UUID id;
    public final String name;


    private List<InetAddress> clientAddressList = new ArrayList<>();

    // all subset namespaces of the current namespace 
    //      e.g. a lobby has teams
    private Namespace[] childrenNamespaces;

    // a controller for the namespace, each namespace will have one, 
    //      it will dictate how commands from incoming messages are handled
    public ControllerInterface controller;


    public Namespace(String name, UUID namespaceUuid) {
        this.name = name;

        // each sibling namespace will have a unique id to differentiate itself from the others
        this.id = namespaceUuid;

        this.controller = new BaseServerController();
    }


    /**
     * Recurrsivley routes through namespace tree to find the namespace which the message is going to be sent to.
     * @param currNamespace
     * @param currNamespaceRouteLst
     * @param message
     */
    public void routeCommand(Namespace currNamespace, String[] currNamespaceRouteLst, Packet message) {
        if (currNamespaceRouteLst.length > 0) {
            // each namespace string within the list is an id which maps to a child namespace if one exists
            String nextNamespace = currNamespaceRouteLst[0];
            UUID nextNamespaceId = UUID.fromString(nextNamespace);
            int indexOfChild = getIndexOfChildNamespaceById(nextNamespaceId);

            // pop the first element in the list
            String[] shortenedNamespaceRouteLst = Arrays.copyOfRange(currNamespaceRouteLst, 1, currNamespaceRouteLst.length);

            routeCommand(childrenNamespaces[indexOfChild], shortenedNamespaceRouteLst, message);
        } else {
            // we have reached the messages intended namespace

            this.controller.handleCommand(currNamespace, message);
        }
    }

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


    public void connectClient(InetAddress clientAddress) {

        this.clientAddressList.add(clientAddress);
    }

    public void disconnectClient(InetAddress clientAddress) {
        this.clientAddressList.remove(clientAddress);
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

    public void setController(ControllerInterface controller) {
        this.controller = controller;
    }

    
}
