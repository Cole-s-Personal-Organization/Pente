package com.mycompany.app.WebServer.Endpoints;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.*;

import com.mycompany.app.WebServer.AbstractNamespace;

public class MyWebSocketServerEndpoint extends Endpoint {

    // Collection to store active sessions
    private static List<Session> activeSessions = new ArrayList<>();


    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // Add the new session to the collection
        activeSessions.add(session);

        // Your additional logic for handling the new connection
        System.out.println("WebSocket connection opened: " + session.getId());
    }

    @Override
    public void onError(Session session, Throwable thr) {
        // TODO Auto-generated method stub
        super.onError(session, thr);
    }


    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    // private void joinLobby(Session session, String )

    private void broadcastMessageToNamespace(Session sender, AbstractNamespace namespace, String message) {

    }

    /**
     * 
     * @param session
     * @param namespace The namespace in which the message is being sent from
     * @param message
     */
    private void sendMessage(Session session, AbstractNamespace namespace, String message) {

    }
}
