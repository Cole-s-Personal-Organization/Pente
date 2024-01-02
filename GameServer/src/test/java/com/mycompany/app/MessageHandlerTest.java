package com.mycompany.app;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mycompany.app.WebServer.Message;
import com.mycompany.app.WebServer.MessageHandler;
import com.mycompany.app.WebServer.Message.MessageAction;
import com.mycompany.app.WebServer.MessageHandler.InvalidMessageConstructionException;

public class MessageHandlerTest {

    @Test
    public void testRemoveQuotesProperlyQuoted() {
        // Test case with properly double-quoted JSON string
        String input = "\"Hello, World!\"";
        String result = MessageHandler.removeQuotesFromStringCastedJson(input);
        assertEquals("Hello, World!", result);
    }

    @Test
    public void testRemoveQuotesImproperlyQuoted() {
        // Test case with improperly quoted JSON string (single quotes used)
        String input = "'Hello, World!'";
        String result = MessageHandler.removeQuotesFromStringCastedJson(input);
        assertEquals("'Hello, World!'", result); // Since the input is not properly quoted, the function should return the input as-is
    }

    @Test
    public void testRemoveQuotesNotQuoted() {
        // Test case with a non-quoted JSON string
        String input = "Hello, World!";
        String result = MessageHandler.removeQuotesFromStringCastedJson(input);
        assertEquals("Hello, World!", result); // Since the input is not quoted, the function should return the input as-is
    }

    @Test
    public void testParseMessage() {
        // Test case with a valid message
        String validMessage = "{\"namespace\":\"ns\",\"endpoint\":\"endpoint\",\"senderId\":\"sender123\",\"recipients\":[\"recipient1\",\"recipient2\"],\"action\":\"GET\",\"data\":\"some data\"}";
        Message validParsedMessage = null;

        try { // ideally there will be no exception, data will be returned
            validParsedMessage = MessageHandler.parseMessage(validMessage);
        } catch (InvalidMessageConstructionException e) {
            assertEquals("Error: missing [senderId] attribute in raw message. Cannot construct Message object.", e.getMessage());
        }

        assertNotNull(validParsedMessage);
        assertEquals("ns", validParsedMessage.namespace);
        assertEquals("endpoint", validParsedMessage.endpoint);
        assertEquals("sender123", validParsedMessage.senderId);
        assertArrayEquals(new String[]{"recipient1", "recipient2"}, validParsedMessage.recipientIds);
        assertEquals(MessageAction.GET, validParsedMessage.action);
        assertEquals(new TextNode("some data"), validParsedMessage.data);
    }

    @Test 
    public void testParseMissingSenderId() {
        String missingSenderIdMessage = "{\"namespace\":\"ns\",\"endpoint\":\"endpoint\",\"recipients\":[\"recipient1\",\"recipient2\"],\"action\":\"GET\",\"data\":\"some data\"}";
        try {
            MessageHandler.parseMessage(missingSenderIdMessage);
            fail("Expected InvalidMessageConstructionException");
        } catch (InvalidMessageConstructionException e) {
            assertEquals("Error: missing [senderId] attribute in raw message. Cannot construct Message object.", e.getMessage());
        }
    } 
    
}
