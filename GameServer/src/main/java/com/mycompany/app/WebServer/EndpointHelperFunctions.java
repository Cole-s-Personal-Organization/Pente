package com.mycompany.app.WebServer;

import java.io.BufferedReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * A class which contains static helper methods for endpoints
 * @author Cole
 */
public class EndpointHelperFunctions {
    
    /**
     * Converts a generic incoming post request body json string into a jsonNode object.
     * @param req request object
     * @return a jsonified post request content object 
     */
    public static JsonNode getPostRequestBody(HttpServletRequest req) {
        final ObjectMapper mapper = new ObjectMapper();
        JsonNode postDataJsonNode = mapper.createObjectNode();

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String body = requestBody.toString();

        try {
            postDataJsonNode = mapper.readTree(body);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return postDataJsonNode;
    }
}
