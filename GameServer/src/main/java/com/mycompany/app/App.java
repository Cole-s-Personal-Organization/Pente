package com.mycompany.app;

import main.java.com.mycompany.app.Pente.PenteGameBoardModel;
import main.java.com.mycompany.app.Pente.PenteGameController;
import main.java.com.mycompany.app.WebServer.MyWebServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {   
        // parse system args
        if (args.length != 1) {
            System.out.println("Number of arguments incorrect.");
            return;
        }
        int portNumber = Integer.parseInt(args[0]);

        System.out.println( "Game Server Starting up..." );

        // MyWebServer webServer = new MyWebServer();

        
        PenteGameBoardModel model = new PenteGameBoardModel();
        // PenteGameController controller = new PenteGameController(null, model);

        // MyWebServer webServer = new MyWebServer(portNumber);

        System.out.println(model.toString());
    }
}
