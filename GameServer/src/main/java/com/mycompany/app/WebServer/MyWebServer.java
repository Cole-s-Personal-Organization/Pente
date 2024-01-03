package com.mycompany.app.WebServer;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import com.mycompany.app.Game.Pente.PenteGameController;
import com.mycompany.app.WebServer.Namespace;
import com.mycompany.app.WebServer.Protocols.BaseProtocol;


public class MyWebServer {
    private final int portNumber;
    
    // Namespaces help organize our clients into logical groupings
    // Namespaces will consititute any subset of the total set of people 
    // For our toy example of pente each lobby will be placed into a Namespace
    //  should they decide to play on teams it will also be necessary to also join the teams together with their own induvidual namespaces

    // it will be up to each namespace to define how 

    private UUID baseNamespaceUuid; 
    private Map<UUID, Namespace> namespaceIdToNamespaceSetMap = new HashMap<>(); 

    public MyWebServer(int portNumber) {
        this.portNumber = portNumber;


        // base namespace - it should be the only active namespace
        Namespace baseNamespace = new Namespace("base");
        this.baseNamespaceUuid = baseNamespace.uuid;
        
        insertNamespace(baseNamespace);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientInstance clientInstance = new ClientInstance(clientSocket);
                
                // every client will be connected to the base namespace so long as they're online
                this.namespaceIdToNamespaceSetMap
                    .get("")
                    .connectClient(clientInstance);

                clientInstance.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The default protocol that handles any requests within the base namespace
     * @param message
     * @throws Exception
     */
    private void baseProtocol(Message message) throws Exception {
        switch (message.endpoint) {
            
            case "chat/":
                break;


            case "namespace/<namespace_id>/join": // 
                break;
        
            default:
                throw new Exception("Invalid endpoint passed to router function.");
        }
    }

    
    private void penteProtocol(Message message) throws Exception {
        switch (message.endpoint) {
            case "":
                break;
        
            default:
                throw new Exception("Invalid endpoint passed to router function.");
        }
    }


    /**
     * Send the message to the appropriate protocol based off namespace
     * @param message
     */
    private void routeMessageByNamespace(Message message) throws Exception {
        switch (message.namespace) {
            case "":
                break;
        
            default:
                throw new Exception("Invalid namespace passed to router function.");
        }
    }

    /**
     * Insert Namespace object into map.
     * Random uuid used
     * @param namespace
     */
    private void insertNamespace(Namespace namespace) {
        

        this.namespaceIdToNamespaceSetMap.put(newNamespaceId, namespace);
    }

    private void removeNamespaceById(UUID namespaceId) {
        this.namespaceIdToNamespaceSetMap.remove(namespaceId);
    }

    
    

    public class ClientInstance extends Thread {
        public final Socket clientSocket;
        private User associatedUserInfo;

        public ClientInstance(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.associatedUserInfo = new User("", "");
        }

        public User getAssociatedUserInfo() {
            return associatedUserInfo;
        }

        /**
         * Function for writing simplified response messages to a user after a message has been sent
         * Generally follows standard HTTP Response codes for ease of use.
         * @param responseCode
         */
        private void writeResponseMessage(int responseCode) {

        }

        private void handleMessage(String rawInputLine) {
            System.out.println(rawInputLine);
            // Message message;

            // try {
            //     message = MessageHandler.parseMessage(rawInputLine);
            // } catch (Exception e) {
            //     // send message to sender that their message just failed
            //     if (e instanceof MessageHandler.InvalidMessageConstructionException) {
            //         writeResponseMessage(422); // unprocessable entity
            //     }
            //     return;
            // }

            // // use the namespace to route the message to it's protocol like an endpoint 
            // String namespace = message.namespace;
            // switch (namespace) {
            //     case "":
            //         // default
            //         break;
            //     default:
            //         // no such namespace, should never be reached
            //         writeResponseMessage(500);
            //         return;
            // }


            // writeResponseMessage(200);
        }

        
        @Override
        public void run() {
            System.out.println("Connection established with " + this.clientSocket.getInetAddress());

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()))) {
                String rawInputLine;
                
                while((rawInputLine = reader.readLine()) != null) {
                    this.handleMessage(rawInputLine);
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
