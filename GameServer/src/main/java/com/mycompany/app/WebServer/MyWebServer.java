package com.mycompany.app.WebServer;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import com.mycompany.app.WebServer.Namespaces.BaseNamespace;
import com.mycompany.app.WebServer.Namespaces.GroupNamespace;


public class MyWebServer {
    private final int portNumber;
    
    private BaseNamespace baseNamespace = new BaseNamespace();
    private Map<String, GroupNamespace> groupNamespaces = new HashMap<>();

    public MyWebServer(int portNumber) {
        this.portNumber = portNumber;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientInstance clientInstance = new ClientInstance(clientSocket);
                this.baseNamespace.connectClient(clientInstance); // every client will be connected to the base namespace so long as they're online

                clientInstance.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public class ClientInstance extends Thread {
        private final Socket clientSocket;
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
