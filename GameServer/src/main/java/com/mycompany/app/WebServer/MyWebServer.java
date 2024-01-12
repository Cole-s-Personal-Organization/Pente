package com.mycompany.app.WebServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.JsonNode;
import com.mycompany.app.WebServer.Packet.PacketBuilder;


public class MyWebServer {
    private final int portNumber;
    private static final int THREAD_POOL_SIZE = 10;

    // webserver keeps track of current sessions of users 
    // NOTE: in the furture, if we expand this to non-local network, we will need to
    //       replace InetAddress with clientId
    private Map<InetAddress, UUID> activateInetAddressToSessionIds;

    private Map<UUID, Socket> sessionIdToSocketMap;
    
    private BaseServerGroup baseServerGroup;

    public MyWebServer(int portNumber) {
        this.portNumber = portNumber;

        // base namespace - grouping of connections that have been authorized by the server
        this.baseServerGroup = new BaseServerGroup(UUID.randomUUID(), "base group", null);

        this.activateInetAddressToSessionIds = new HashMap<>();
        this.sessionIdToSocketMap = new HashMap<>();

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

    public void processRawStringPacket(Socket senderSocket, String rawStringPacket) {
        Packet packet = null;
        Packet responsePacket = null;

        try {
            PacketBuilder builder = new Packet.PacketBuilder(rawStringPacket);

            // attach optional session id
            if (this.activateInetAddressToSessionIds.containsKey(senderSocket.getInetAddress())) {
                UUID sessionId = this.activateInetAddressToSessionIds.get(senderSocket.getInetAddress());
                builder.setClientSessionId(sessionId);
            }

            packet = builder.build();

        } catch (Packet.InvalidPacketConstructionException e) {
            // TODO: handle exception
        } catch (IOException e) {
            // TODO: handle exception
            return;
        }

        boolean isAuthorized = this.checkMessageSenderAuthorization(senderSocket);
 
        if(!isAuthorized) {
            responsePacket = handleMessageFromUnauthorizedSender(senderSocket, packet); 
            writePacketToSocketStream(senderSocket, responsePacket);
            return;
        }

        AbstractNamespace targetNamespace;

        try {
            targetNamespace = getTargetNamespace(packet.namespacePath);
        } catch (NoSuchElementException e) {
            // TODO: handle exception

            return;
        }
        
        responsePacket = targetNamespace.handlePacket(packet);

        // get all sockets which need to be written to 
    }

    /**
     * Send message to client
     * @param message
     * @param clientId
     */
    public void sendMessage(UUID sessionId, Packet p) {
        Socket socket = this.sessionIdToSocketMap.get(sessionId);

        writePacketToSocketStream(socket, p);
    }

    /**
     * Send message to all clients in namespace as well as children
     * @param message
     */
    // public void broadcastMessage(Packet message) {
    //     List<Sockets> sess = new ArrayList<>(this.clientIdToInstanceMap.values());

    //     for (ClientInstance clientInstance : clients) {
    //         writePacketToSocketStream(null, message);
    //     }
    // }

    private void writePacketToSocketStream(Socket s, Packet p) {
        try {
            OutputStream outputStream = s.getOutputStream();

            outputStream.write(p.toSendString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get a namespace by its namespacePath
     * @param namespacePath
     * @return
     * @throws NoSuchElementException
     */
    private AbstractNamespace getTargetNamespace(String[] namespacePath) throws NoSuchElementException {
        AbstractNamespace currNamespace = this.baseServerGroup;

        for (String path : namespacePath) {
            // when we're here, we have another child to find 
            boolean nextChildNamespaceFound = false;

            if (currNamespace.childrenNamespaces.isEmpty()) {
                throw new NoSuchElementException();
            }

            int childNamespaceIterator = 0;
            AbstractNamespace childNamespace; 

            while(currNamespace.childrenNamespaces.size() <= childNamespaceIterator) {
                childNamespace = currNamespace.childrenNamespaces.get(childNamespaceIterator);
                if (childNamespace.getName() == path) {
                    currNamespace = childNamespace;
                    nextChildNamespaceFound = true;
                    break;
                }
            }

            if (!nextChildNamespaceFound) {
                throw new NoSuchElementException();
            }
        }

        return currNamespace;
    }

    /**
     * Check if a socket has been connected to the webserver 
     * @param s
     * @return
     */
    private boolean checkMessageSenderAuthorization(Socket s) {
        InetAddress address = s.getInetAddress();
        return this.activateInetAddressToSessionIds.containsKey(address);
    }


    /**
     * Exposes the root level join server command, prevents any non joining packets 
     * from being processed.
     * @param p
     */
    private Packet handleMessageFromUnauthorizedSender(Socket s, Packet p) {
        JsonNode payload = p.data;
        String clientName = payload.get("clientName").asText();

        String errMsg = "";
        
        switch (p.command) {
            case "GRREEETINGS":
                
                UUID clientId = getClientID();
                UUID sessionId = this.rollNewUniqueSessionID();

                // create clientProxy
                ClientProxy proxy = new ClientProxy(clientId, sessionId, clientName);


                // enter client into base level namespace
                this.activateInetAddressToSessionIds.put(s.getInetAddress(), sessionId);
                this.baseServerGroup.connectClient(sessionId, proxy);



                // init clientProxy replication manager
                ReplicationManagerService rManagerService = new ReplicationManagerService();
                proxy.setReplicationManagerService(rManagerService);

                Packet welcomePacket = PacketHelpers.createWelcomePacket(clientName);
                return welcomePacket;

            default:
                errMsg = "Bad incoming packet from unknown client at socket " + s.getInetAddress().toString();
                System.out.println(errMsg);
                break;
        }

        return PacketHelpers.createErrorPacket(p, clientName);
    }

    private UUID rollNewUniqueSessionID() {
        UUID sessionId = UUID.randomUUID();
        Collection<UUID> takenSessionIds = this.activateInetAddressToSessionIds.values();

        while(takenSessionIds.contains(sessionId)) {
            sessionId = UUID.randomUUID();
        }

        return sessionId;
    }

    private UUID getClientID() {
        // for now just set to a random id, needs to change later if we plan to make users
        return UUID.randomUUID();
    }


    /**
     * Runnable that handles client connections to the server
     */
    private class ServerRunnable implements Runnable {
        private Socket clientSocket;

        public ServerRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("Connection established with " + this.clientSocket.getInetAddress());

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()))) {
                String rawInputLine;
                
                while((rawInputLine = reader.readLine()) != null) {
                    processRawStringPacket(this.clientSocket, rawInputLine);
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
