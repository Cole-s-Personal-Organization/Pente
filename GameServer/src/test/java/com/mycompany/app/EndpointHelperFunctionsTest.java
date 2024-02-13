package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.Game.Pente.PenteBoardIdentifierEnum;
import com.mycompany.app.Game.Pente.PenteTurn;
import com.mycompany.app.WebServer.Models.PenteGameEvent;
import com.mycompany.app.WebServer.Models.PenteGameEvent.PenteGameEventType;
import com.mycompany.app.WebServer.WebServlets.EndpointHelperFunctions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class EndpointHelperFunctionsTest {
    @Test
    public void testGetPostRequestBody_Success() throws IOException {
        String requestBody = "{\"name\": \"John\", \"age\": 30}";
        HttpServletRequest request = mock(HttpServletRequest.class);
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);

        JsonNode expectedJson = new ObjectMapper().readTree(requestBody);
        JsonNode result = EndpointHelperFunctions.getPostRequestBody(request);

        assertEquals(expectedJson, result);
    }

    @Test
    public void testGetPostRequestBody_EmptyBody() throws IOException {
        String requestBody = "";
        HttpServletRequest request = mock(HttpServletRequest.class);
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);

        JsonNode result = EndpointHelperFunctions.getPostRequestBody(request);

        assertEquals(null, result);
    }

    @Test
    public void testGetPostRequestBody_InvalidJson() throws IOException {
        String requestBody = "This is not a JSON";
        HttpServletRequest request = mock(HttpServletRequest.class);
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);

        JsonNode result = EndpointHelperFunctions.getPostRequestBody(request);

        assertEquals(null, result);
    }

    @Test
    public void testParseGameEvent_ValidInput() throws JsonProcessingException {
        UUID eventId = UUID.randomUUID();
        PenteGameEventType eventType = PenteGameEventType.TurnTaken;
        int posX = 3;
        int posY = 4;
        PenteBoardIdentifierEnum playerNumber = PenteBoardIdentifierEnum.PLAYER1;
        PenteTurn turn = new PenteTurn.PenteTurnBuilder(posX, posY, playerNumber)
                .setTurnOneAction(true)
                .build();

        String validGameEvent = "{\"eventId\": \"" + eventId + "\", \"event\": \"" + eventType + "\", \"data\": {\"turn\": " + new ObjectMapper().writeValueAsString(turn) + "}}";
        PenteGameEvent parsedEvent = EndpointHelperFunctions.parseGameEvent(validGameEvent);
        assertNotNull(parsedEvent);
        assertEquals(eventId.toString(), parsedEvent.getEventId().toString());
        assertEquals(PenteGameEventType.TurnTaken, parsedEvent.getEvent());
        assertEquals("{\"turn\":" + new ObjectMapper().writeValueAsString(turn) + "}", parsedEvent.getData().toString());
    }

    @Test
    public void testParseGameEvent_InvalidInput() throws JsonProcessingException {
        UUID eventId = UUID.randomUUID();
        PenteGameEventType eventType = PenteGameEventType.TurnTaken;
        int posX = 3;
        int posY = 4;
        PenteBoardIdentifierEnum playerNumber = PenteBoardIdentifierEnum.PLAYER1;
        PenteTurn turn = new PenteTurn.PenteTurnBuilder(posX, posY, playerNumber)
                .setTurnOneAction(true)
                .build();
            
        String invalidGameEvent = "{\"eventId\": \"" + eventId + "\", \"event\": \"" + "Invalid" + "\", \"data\": {\"turn\": " + new ObjectMapper().writeValueAsString(turn) + "}}";
        PenteGameEvent parsedEvent = EndpointHelperFunctions.parseGameEvent(invalidGameEvent);
        assertNull(parsedEvent);
    }

    @Test
    public void testParseGameEvent_NullInput() {
        String nullGameEvent = null;
        PenteGameEvent parsedEvent = EndpointHelperFunctions.parseGameEvent(nullGameEvent);
        assertNull(parsedEvent);
    }
}
