package com.mycompany.app;

import com.mycompany.app.Game.Pente.PenteGameBoardModel;
import com.mycompany.app.Game.Pente.PenteGameController;
import com.mycompany.app.WebServer.MyWebServer;

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

        MyWebServer webServer = new MyWebServer(portNumber);

        webServer.start();
    }
}
