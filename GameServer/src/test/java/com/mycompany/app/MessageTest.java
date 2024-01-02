package com.mycompany.app;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import org.junit.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.WebServer.Message;

public class MessageTest {

    @Test
    public void testToSendStringBasic() {
        String namespace = "testNamespace";
        String endpoint = "testEndpoint";
        String action = "POST";
        String senderId = "testSenderId";
        String[] recipientIds = {"recipient1", "recipient2"};
        JsonNode data = new ObjectMapper().createObjectNode(); // blank data

        Message message = new Message(namespace, endpoint, action, senderId, recipientIds, data);

        String expected = "{" +
            "\"namespace\":\"" + namespace + "\"," +
            "\"endpoint\":\"" + endpoint + "\"," +
            "\"senderId\":\"" + senderId + "\"," +
            "\"action\":\"" + Message.MessageAction.POST + "\"," +
            "\"data\":\"" + data + "\"," +
            "\"recipients\":[\"recipient1\",\"recipient2\"]" +
            "}";
        assertEquals(expected, message.toSendString());
    }

    @Test
    public void testToSendStringEmptyRecipientsArray() {
        String namespace = "emptyRecipientsNamespace";
        String endpoint = "emptyRecipientsEndpoint";
        String action = "GET";
        String senderId = "emptyRecipientsSenderId";
        String[] recipientIds = {};
        JsonNode data = new ObjectMapper().createObjectNode(); // blank data

        Message message = new Message(namespace, endpoint, action, senderId, recipientIds, data);

        String expected = "{" +
            "\"namespace\":\"" + namespace + "\"," +
            "\"endpoint\":\"" + endpoint + "\"," +
            "\"senderId\":\"" + senderId + "\"," +
            "\"action\":\"" + Message.MessageAction.GET + "\"," +
            "\"data\":\"" + data + "\"," +
            "\"recipients\":[]" +
            "}";
        assertEquals(expected, message.toSendString());
    }

    @Test
    public void testToSendStringEmptyEndpoint() {
        String namespace = "emptyEndpointNamespace";
        String endpoint = "";
        String action = "UPDATE";
        String senderId = "emptyEndpointSenderId";
        String[] recipientIds = {"recipient6"};
        JsonNode data = new ObjectMapper().createObjectNode(); // blank data

        Message message = new Message(namespace, endpoint, action, senderId, recipientIds, data);

        String expected = "{" +
            "\"namespace\":\"" + namespace + "\"," +
            "\"endpoint\":\"\"," +
            "\"senderId\":\"" + senderId + "\"," +
            "\"action\":\"" + Message.MessageAction.UPDATE + "\"," +
            "\"data\":\"" + data + "\"," +
            "\"recipients\":[\"recipient6\"]" +
            "}";
        assertEquals(expected, message.toSendString());
    }
}

