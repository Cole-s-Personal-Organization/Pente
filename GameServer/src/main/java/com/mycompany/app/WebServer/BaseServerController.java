package com.mycompany.app.WebServer;

public class BaseServerController implements ControllerInterface {
    @Override
    public int handleMessage(Message message) {
        int responseCode = 500; // by default throw server error

        switch (message.command) {
            case "connect user": // connect user to namespace

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
