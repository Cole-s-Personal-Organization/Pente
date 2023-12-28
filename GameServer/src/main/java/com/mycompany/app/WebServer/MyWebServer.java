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
                System.out.println("Connection established with " + clientSocket.getInetAddress());

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
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()))) {
                String rawInput = reader.readLine();
                System.out.println(rawInput);
                // JSONObject deserializedJson;
                
                // try {
                //     deserializedJson = new JSONObject(rawInput);
                //     System.out.println(deserializedJson.toString(2));
                // } catch (JSONException e) {
                //     e.printStackTrace();
                // }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Close the client socket
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
