package com.mycompany.app.WebServer;

import java.util.ArrayList;
import java.util.HashMap;

public class GamePool {
    private HashMap<String, GameLobby> lobbies;

    public GamePool() {
        this.lobbies = new HashMap<String, GameLobby>();
    }

    public GameLobby getLobbyByName(String lobbyName) {
        return this.lobbies.get(lobbyName);
    }

    public ArrayList<GameLobby> getLobbies() {
        return (ArrayList<GameLobby>)this.lobbies.values();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
