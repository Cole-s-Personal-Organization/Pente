package com.bingus.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        // Change the server address and port based on your server configuration
        String serverAddress = "localhost";
        int serverPort = 8000;

        try {
            // Connect to the server
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to the server");

            // Set up reader for console input
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            // Set up writer to send data to the server
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);

            // Read input from the console and send it to the server
            while (true) {
                System.out.print("Enter text to send to the server (or 'exit' to quit): ");
                String userInput = consoleReader.readLine();

                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }

                // Send the input to the server
                serverWriter.println(userInput);
            }

            // Close resources
            consoleReader.close();
            serverWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
