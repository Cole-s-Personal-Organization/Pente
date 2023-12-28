package main.java.com.mycompany.app.WebServer;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

public class MyWebServer {
    private final int portNumber;

    public MyWebServer(int portNumber) {
        this.portNumber = portNumber;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established with " + clientSocket.getInetAddress());

                // Create a new thread to handle the client request
                MyWebServerRunnable clientMessageHandler = new MyWebServerRunnable();
                Thread clientThread = new Thread(clientMessageHandler);
                // Thread clientThread = new Thread(() -> handleClientRequest(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream()
        ) {
            // Read the incoming message
            String request = reader.readLine();
            System.out.println("Received message: " + request);

            // Send a response back to the client
            String response = "Hello from the server!";
            outputStream.write(response.getBytes());

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

    public class MyWebServerRunnable implements Runnable {
        private String data;

        public MyWebServerRunnable(String requestData) {
            this.data = requestData;

        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            System.out.println("test from runnable");
        }
    }
}
