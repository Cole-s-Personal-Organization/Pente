package com.mycompany.app.WebServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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

    public void processRawStringPacket(Socket senderSocket, String rawStringPacket) {
        Packet packet = null;

        try {
            packet = new Packet.PacketBuilder(rawStringPacket)
                .setSenderSocketAddress(senderSocket.getInetAddress())
                .build();

        } catch (Packet.InvalidPacketConstructionException e) {
            // TODO: handle exception
        } catch (IOException e) {
            // TODO: handle exception
            return;
        }


        AbstractNamespace targetNamespace;

        try {
            targetNamespace = getTargetNamespace(packet.namespacePath);
        } catch (NoSuchElementException e) {
            // TODO: handle exception

            return;
        }
        
        Packet responsePacket = targetNamespace.handlePacket(packet);

        // get all sockets which need to be written to 
    }

    /**
     * Send message to client within namespace
     * @param message
     * @param clientId
     */
    public void sendMessage(Packet message, UUID sessionId) {
        Socket socket = this.sessionIdToClientProxyMap.get(sessionId).;

        try {
            OutputStream outputStream = socket.getOutputStream();

            outputStream.write(message.toSendString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send message to all clients in namespace as well as children
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

    private AbstractNamespace getTargetNamespace(String[] namespacePath) throws NoSuchElementException {
        AbstractNamespace currNamespace = this.baseServerGroup;

        for (String path : namespacePath) {
            // when we're here, we have another child to find 
            boolean nextChildNamespaceFound = false;

            if (currNamespace.childrenNamespaces.length <= 0) {
                throw new NoSuchElementException();
            }

            int childNamespaceIterator = 0;
            AbstractNamespace childNamespace; 

            while(currNamespace.childrenNamespaces.length <= childNamespaceIterator) {
                childNamespace = currNamespace.childrenNamespaces[childNamespaceIterator];
                if (childNamespace.name == path) {
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
