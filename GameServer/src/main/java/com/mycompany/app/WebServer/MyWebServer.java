package main.java.com.mycompany.app.WebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyWebServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Server waiting for client connection...");

            // Accept client connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected!");

            // Perform socket operations as needed

            // Close sockets
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
