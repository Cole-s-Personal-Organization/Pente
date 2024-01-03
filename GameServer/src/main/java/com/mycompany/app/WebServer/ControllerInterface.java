package com.mycompany.app.WebServer;

public interface ControllerInterface {
    // returns http inspired numeric code for success/faliure
    public int handleMessage(Message message); 
}
