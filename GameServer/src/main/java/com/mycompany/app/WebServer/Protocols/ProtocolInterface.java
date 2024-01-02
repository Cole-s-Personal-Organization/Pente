package com.mycompany.app.WebServer.Protocols;

import com.mycompany.app.WebServer.Message;

public interface ProtocolInterface {
    // returns http inspired numeric code for success/faliure
    public int handleMessage(Message message); 
}
