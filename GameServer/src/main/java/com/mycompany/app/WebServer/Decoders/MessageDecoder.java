package com.mycompany.app.WebServer.Decoders;

import java.io.StringReader;

import com.mycompany.app.WebServer.Models.Message;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(String jsonMessage) throws DecodeException {
        Message message = new Message();

        JsonObject jsonObject = Json
            .createReader(new StringReader(jsonMessage)).readObject();
            
        message.setFrom(jsonObject.getString("username"));
        message.setContent(jsonObject.getString("message"));
        message.setContext(jsonObject.getString("context"));

        return message;
    }

    @Override
    public boolean willDecode(String jsonMessage) {
    try {
        // Check if incoming message is valid JSON
        Json.createReader(new StringReader(jsonMessage)).readObject();
        return true;
    } catch (Exception e) {
        return false;
    }
    }

    @Override
    public void destroy() {
        System.out.println("Destroyed message decoder");
    }

    @Override
    public void init(EndpointConfig config) {
        System.out.println("Initializing message decoder");
    }
    
    
}
