package com.mycompany.app.WebServer.Endpoints;

import java.io.*;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import com.mycompany.app.WebServer.Models.Message;

@ServerEndpoint(value = "/chat/{context}")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) throws IOException {
        // Get session and WebSocket connection
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException {
        // Handle new messages
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // WebSocket connection closes
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }
}