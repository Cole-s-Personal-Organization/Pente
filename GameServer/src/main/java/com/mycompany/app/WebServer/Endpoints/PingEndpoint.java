package com.mycompany.app.WebServer.Endpoints;

import java.io.*;

import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.*;

@ServerEndpoint(value="/ping")
public class PingEndpoint extends Endpoint {
    private Session session;

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        // TODO Auto-generated method stub
        super.onClose(session, closeReason);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        // TODO Auto-generated method stub
        super.onError(session, thr);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            
            @Override 
            public void onMessage(String message) {
                System.out.println("retrieved: " + message);
            }
        });
    }
    
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
