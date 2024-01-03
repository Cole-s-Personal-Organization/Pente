package com.mycompany.app.WebServer.Protocols;

import com.mycompany.app.WebServer.Message;
import com.mycompany.app.WebServer.Namespace;

public class BaseProtocol implements ProtocolInterface {

    public int handleMessage(Namespace namespace, Message message) {
        // based on namespace passed in, find its controller type in order to 
        String namespaceControllerType = "";


        int responseCode = 500; // by default throw server error

        // handle any high level commands related to the overall namespace here
        switch (message.command) {
            case "connect user": // connect user to namespace
                namespace.connectClient(null, null);
                break;

            case "chat":
                
                break;
            case "start lobby":

            case "end lobby":

            
            default:
                break;
        }

        return responseCode;
    }
}
