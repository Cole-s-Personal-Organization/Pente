package com.mycompany.app.WebServer;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class MyWebServer {
    private final int portNumber;
    private Map<String, Set<ClientInstance>> namespaceToClientsMap = new HashMap<String, Set<ClientInstance>>();

    private GamePool gamePool;

    public MyWebServer(int portNumber) {
        this.portNumber = portNumber;

        // by default, every user is connected to the "" namespace
        namespaceToClientsMap.put("", new HashSet<ClientInstance>()); 

        this.gamePool = new GamePool();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientInstance clientInstance = new ClientInstance(clientSocket);
                this.addClientToNamespace("", clientInstance);

                clientInstance.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addClientToNamespace(String namespace, ClientInstance client) {
        this.namespaceToClientsMap.get(namespace).add(client);
    }

    public void removeClientFromNamespace(String namespace, ClientInstance client) {
        this.namespaceToClientsMap.get(namespace).remove(client);
    }

    public void removeClientFromAllNamespaces(ClientInstance client) {

    }

    public void broadcastMessageInNamespace(String namespace, Message message) {

    }

    public void sendMessageToRecipientsInNamespace(String namespace, Message message, ClientInstance[] recipients) {
        if (recipients.length > 0) {
            
        }
    }

    

    public class ClientInstance extends Thread {
        private final Socket clientSocket;
        private User associatedUserInfo;

        public ClientInstance(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.associatedUserInfo = new User("", "");
        }

        /**
         * Function for writing simplified response messages to a user after a message has been sent
         * Generally follows standard HTTP Response codes for ease of use.
         * @param responseCode
         */
        private void writeResponseMessage(int responseCode) {

        }

        private void handleMessage(String rawInputLine) {
            Message message;

            try {
                message = MessageHandler.parseMessage(rawInputLine);
            } catch (Exception e) {
                // send message to sender that their message just failed
                if (e instanceof MessageHandler.InvalidMessageConstructionException) {
                    writeResponseMessage(422); // unprocessable entity
                }
                return;
            }

            // use the namespace to route the message to it's protocol like an endpoint 
            String namespace = message.namespace;
            switch (namespace) {
                case "":
                    // default
                    break;
                default:
                    // no such namespace, should never be reached
                    writeResponseMessage(500);
                    return;
            }


            writeResponseMessage(200);
            

            /**
             * from this point we need to 
             * 1. parse the string into a format which is readable to a message director (decides where the message needs to go)
             * 2. identify what the intention of the message is
             *  - is it a player turn
             *  - is a player trying to join a lobby
             *  - no action or not a allowed action?
             *  - etc
             * 3. data needs to be formated in a manner to allow it be taken in from its recipients
             * 4. formatted data alters models
             * 5. state is now changed, concerned parties now need to be made aware, notify 
             */

                // parse 
            //  Message message = new Message(rawInputLine);

            // determine what to build based on parsed message namespace

            // switch(message.namespace) {
            //     case "all":
            //         return
            //     case "pente"
            // }
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
