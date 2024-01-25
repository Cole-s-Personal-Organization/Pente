package com.mycompany.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class PenteEndpointIntegrationTests {
    
    // @Test
    // public void getAllGames() {
    //     try {
    //         // Specify the URL of your servlet endpoint
    //         URL url = new URL("http://localhost:8080/your-servlet-endpoint");

    //         // Open a connection to the URL
    //         HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    //         // Set the request method to GET
    //         connection.setRequestMethod("GET");

    //         // Get the response code
    //         int responseCode = connection.getResponseCode();
    //         System.out.println("Response Code: " + responseCode);

    //         // Read the response content
    //         try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
    //             String line;
    //             StringBuilder responseContent = new StringBuilder();

    //             while ((line = reader.readLine()) != null) {
    //                 responseContent.append(line);
    //             }

    //             // Print the response content
    //             System.out.println("Response Content: " + responseContent.toString());
    //         }

    //         // Close the connection
    //         connection.disconnect();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
        
    // }
}
