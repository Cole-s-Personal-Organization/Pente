package com.mycompany.app.WebServer.Namespaces;

import java.util.*;
import com.mycompany.app.WebServer.Message;
import com.mycompany.app.WebServer.MyWebServer.ClientInstance;

public class BaseNamespace extends AbstractNamespace {

    @Override
    public int handleMessage(Message message) {
        int responseCode = 500; // by default throw server error

        switch (message.endpoint) {
            case "chat":
                
                break;
        
            default:
                break;
        }

        return responseCode;
    }
}
