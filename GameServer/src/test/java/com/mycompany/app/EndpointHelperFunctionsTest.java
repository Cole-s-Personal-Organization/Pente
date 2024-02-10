package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}
