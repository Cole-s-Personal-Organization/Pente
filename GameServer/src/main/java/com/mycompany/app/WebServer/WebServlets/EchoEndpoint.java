package com.mycompany.app.WebServer.WebServlets;

import java.io.IOException;
import java.nio.ByteBuffer;

import jakarta.websocket.OnMessage;
import jakarta.websocket.PongMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket/echo")
public class EchoEndpoint {

    @OnMessage
    public void echoTextMessage(Session session, String msg, boolean last) {
        System.out.println("Beginning handling of message at echo endpoint.");
        System.out.println("Incoming message: " + msg);

        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(msg, last);
                System.out.println("Successfully Handled Message.");
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    @OnMessage
    public void echoBinaryMessage(Session session, ByteBuffer bb,
            boolean last) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendBinary(bb, last);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    /**
     * Process a received pong. This is a NO-OP.
     *
     * @param pm    Ignored.
     */
    @OnMessage
    public void echoPongMessage(PongMessage pm) {
        // NO-OP
    }
}