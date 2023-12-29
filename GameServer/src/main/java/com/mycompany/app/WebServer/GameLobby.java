package com.mycompany.app.WebServer;

import java.util.*;

import com.mycompany.app.Game.Pente.PenteGameController;

public class GameLobby {
    private String lobbyName;
    private String lobbyHostUid;
    private Set<User> connectedPlayers = new HashSet<>();
    private ArrayList<String> displayLog; // a log for text that can be seen by all players

    private GameControllerInterface gameController;

    public GameLobby(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public void selectGame(String gameName) {
        switch (gameName) {
            case "Pente":
                this.gameController = new PenteGameController(null, null);
                break;
        
            default:
                break;
        }
    }


    public void joinLobby(User user) {
        if (this.connectedPlayers.size() == 0) {
            this.connectedPlayers.add(user);
        } 

        // message to all within group that new user has joined
        Message justJoinedMessage = new Message(lobbyHostUid);
        // this.connectedPlayers.
    }

    public void leaveLobby(User user) {

    }


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }
}
