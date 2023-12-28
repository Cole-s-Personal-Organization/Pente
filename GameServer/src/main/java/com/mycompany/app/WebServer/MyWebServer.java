package com.mycompany.app.WebServer;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class MyWebServer {
    private final int portNumber;

    private static List<Socket> connectedClients = new ArrayList<>();

    public MyWebServer(int portNumber) {
        this.portNumber = portNumber;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                connectedClients.add(clientSocket);

                // Create a new thread to handle the client request
                ClientRunnable clientMessageHandler = new ClientRunnable(clientSocket);
                Thread clientThread = new Thread(clientMessageHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    public class ClientRunnable implements Runnable {
        private final Socket clientSocket;

        public ClientRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }


        @Override
        public void run() {
            System.out.println("Connection established with " + this.clientSocket.getInetAddress());

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()))) {
                String rawInputLine;
                
                while((rawInputLine = reader.readLine()) != null) {
                    System.out.println(rawInputLine);

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
                     Message message = new Message(rawInputLine);

                    // determine what to build based on parsed message namespace
                    
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
