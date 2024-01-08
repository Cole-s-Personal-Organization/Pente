package com.mycompany.app.WebServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.JsonNode;

import com.mycompany.app.Game.Pente.PenteGameController;
import com.mycompany.app.WebServer.PacketHandler.InvalidPacketConstructionException;


public class MyWebServer {
    private final int portNumber;
    private static final int THREAD_POOL_SIZE = 10;
    
    private UnauthedConnectionGroup waitingGroup;
    private BaseServerGroup baseServerGroup;

    public MyWebServer(int portNumber) {
        this.portNumber = portNumber;


        // waiting group - all connections that have made attempts to connect to the server
        //                 if a user has not been officially connected, they will appear here
        this.waitingGroup = new UnauthedConnectionGroup("wait group", UUID.randomUUID());

        // base namespace - grouping of connections that have been authorized by the server
        this.baseServerGroup = new BaseServerGroup("base group", UUID.randomUUID());
    }

    /**
     * Starts the webserver on specified port
     */
    public void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // start server 
        try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();


                ServerRunnable newRunnable = new ServerRunnable(clientSocket);
                    
                executorService.submit(newRunnable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    public boolean isClientConnectedToServer(InetAddress address) {
        return (this.addressToClientProxyMap.get(address) != null);
    }

    public Packet processRawStringPacket(String rawStringPacket) {
        Packet packet = null;

        try {
            packet = PacketHandler.parsePacket(rawStringPacket);
            // String intendedNamespace = packet.namespace;
            // String[] intendedNamespaceStrArr = intendedNamespace.split("/"); 
            // baseNamespace.routeCommand(intendedNamespace, message);

        } catch (InvalidPacketConstructionException e) {
            // TODO: handle exception
        }

        return packet;
    }

    /**
     * Initiates recurrsive call for handling packets within the namespace data structure.
     * @param packet
     * @param fromAddress
     */
    private void handlePacketFromKnownClient(Packet packet, InetAddress fromAddress) {
        String[] baseNamespacePath = packet.getNamespacePath();
        
        this.baseNamespace.routeCommand(baseNamespace, baseNamespacePath, packet);
    }

    /**
     * Handles new connections to the base of the server
     * @param packet
     * @param fromAddress
     */
    public void handlePacketFromNewClient(Packet packet, InetAddress fromAddress) {
        switch (packet.getCommand()) {
            case "HELLO":
                JsonNode payload = packet.getData();
                String clientName = payload.get("clientName").asText();
                UUID clientId = UUID.randomUUID();

                // create clientProxy
                ClientProxy proxy = new ClientProxy(fromAddress, clientName, clientId);


                // enter client into base level namespace
                this.baseNamespace.connectClient(fromAddress);

                // welcome client - use connected base controller to send welcome message
                this.baseNamespace.controller.welcomeNewClient();


                // init clientProxy replication manager
                ReplicationManagerService rManagerService = new ReplicationManagerService();
                proxy.setReplicationManagerService(rManagerService);

                this.addressToClientProxyMap.put(fromAddress, proxy);
                break;

            default:
                System.out.println("Bad incoming packet from unknown client at socket " + fromAddress.toString());
                break;
        }
    }


    /**
     * Runnable that handles client connections to the server
     */
    private class ServerRunnable implements Runnable {
        private Socket clientSocket;

        public ServerRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        private void routePacketBasedOnClientDiscoveryStatus(Packet packet) {
            // do we know who this is
            InetAddress clientAddress = this.clientSocket.getInetAddress();
            boolean isClientConnected = isClientConnectedToServer(clientAddress);

            if (isClientConnected) {
                handlePacketFromKnownClient(packet, clientAddress);
            } else {
                handlePacketFromNewClient(packet, clientAddress);
            }
        }

        @Override
        public void run() {
            System.out.println("Connection established with " + this.clientSocket.getInetAddress());

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()))) {
                String rawInputLine;
                
                while((rawInputLine = reader.readLine()) != null) {
                    Packet incomingPacket = processRawStringPacket(rawInputLine);

                    if (incomingPacket != null) {
                        routePacketBasedOnClientDiscoveryStatus(incomingPacket);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Close the client socket
                    System.out.println("Connection closed with " + this.clientSocket.getInetAddress());
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
