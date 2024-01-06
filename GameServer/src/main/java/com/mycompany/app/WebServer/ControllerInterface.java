package com.mycompany.app.WebServer;

public interface ControllerInterface {
    public void processPacket(Namespace namespace, Packet packet);

    // public void handlePacketFromNewClient();

    public void welcomeNewClient();

    // public String getCommands();
}
