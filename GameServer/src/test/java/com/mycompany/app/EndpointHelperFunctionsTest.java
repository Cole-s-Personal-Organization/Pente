package com.mycompany.app;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.WebServer.EndpointHelperFunctions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class EndpointHelperFunctionsTest {
    // @Test
    // public void testGetPostRequestBody_Success() throws IOException {
    //     String requestBody = "{\"name\": \"John\", \"age\": 30}";
    //     HttpServletRequest request = new MockHttpServletRequest(requestBody);

    //     JsonNode expectedJson = new ObjectMapper().readTree(requestBody);
    //     JsonNode result = EndpointHelperFunctions.getPostRequestBody(request);

    //     assertEquals(expectedJson, result);
    // }

    // public class MockHttpServletRequest extends HttpServletRequestWrapper {
    //     private final String body;

    //     public MockHttpServletRequest(String body) {
    //         super(null); // Pass null to super constructor since we don't need the original request
    //         this.body = body;
    //     }

    //     @Override
    //     public BufferedReader getReader() {
    //         return new BufferedReader(new StringReader(body));
    //     }
    // }
}
