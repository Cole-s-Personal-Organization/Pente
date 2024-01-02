package com.mycompany.app.WebServer.Namespaces;

import java.util.*;

import com.mycompany.app.WebServer.Message;
import com.mycompany.app.WebServer.MyWebServer.ClientInstance;

public abstract class AbstractNamespace {
    private Map<Integer, ClientInstance> clientHashToClientMap = new HashMap<>();

    // returns http inspired numeric code for success/faliure
    public abstract int handleMessage(Message message);

    public void connectClient(ClientInstance client) {
        this.clientHashToClientMap.put(client.hashCode(), client);
    }

    public void disconnectClient(ClientInstance client) {
        this.clientHashToClientMap.remove(client.hashCode());
    }

    public void sendMessage(Message message, ClientInstance client) {
        
    }

    public void broadcastMessage(Message message) {

    }

}
