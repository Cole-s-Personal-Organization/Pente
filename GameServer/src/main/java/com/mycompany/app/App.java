package com.mycompany.app;

import main.java.com.mycompany.app.Pente.PenteGameBoardModel;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Game Server Starting up..." );

        // MyWebServer webServer = new MyWebServer();

        PenteGameBoardModel model = new PenteGameBoardModel();

        System.out.println(model.toString());
    }
}
