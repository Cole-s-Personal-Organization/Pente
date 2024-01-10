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
    
    private BaseServerGroup baseServerGroup;

    public MyWebServer(int portNumber) {
        this.portNumber = portNumber;

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
