package com.mycompany.app.WebServer;

import javax.websocket.server.ServerEndpointConfig;

import java.net.URI;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket")
public class MyWebSocketServer {
    
    // public static void main(String[] args) {
    //     final String uriValue = "ws://localhost:8080/contextPath/websocket";

    //     ServerEndpointConfig serverEndpointConfig = ServerEndpointConfig.Builder.create(MyWebSocketEndpoint.class, "/websocket").build();
    //     WebSocketContainer container = ContainerProvider.getWebSocketContainer();

    //     try {
    //         container.connectToServer(MyWebSocketEndpoint.class, serverEndpointConfig, new URI(uriValue));
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}
