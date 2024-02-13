package com.mycompany.app.WebServer.WebServlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;
import java.lang.IllegalArgumentException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.WebServer.Models.PenteGameEvent;
import com.mycompany.app.WebServer.Models.PenteGameEvent.PenteGameEventType;

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
        JsonNode postDataJsonNode = null;

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

    public static PenteGameEvent parseGameEvent(String gameEvent) {
        final ObjectMapper mapper = new ObjectMapper();
        JsonNode gameEventNode = null;

        UUID eventId = null;
        PenteGameEventType event = null;
        JsonNode data = null;

        if (gameEvent == null) {
            return null;
        }

        try {
            gameEventNode = mapper.readTree(gameEvent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String deserializedGameId = gameEventNode.get("eventId").asText();
        if (deserializedGameId != null) {
            eventId = UUID.fromString(deserializedGameId);
        } else {
            return null;
        }

        String deserializedPenteGameEventType = gameEventNode.get("event").asText();
        if (deserializedPenteGameEventType != null) {
            try {
                event = PenteGameEventType.valueOf(deserializedPenteGameEventType);
            } catch (IllegalArgumentException e) {
                System.out.println("Illegal event type received.");
            }
        } else {
            return null;
        }

        JsonNode dataNode = gameEventNode.get("data");
        if (dataNode != null) {
            data = dataNode;
        } else {
            return null;
        }

        if (eventId == null || event == null || data == null) {
            return null;
        }

        return new PenteGameEvent(eventId, event, data);
    }
}
