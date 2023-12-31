package com.mycompany.app.Game;

public class Player {
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public Player(String displayName) {
        this.displayName = displayName; 
    }

    @Override
    public String toString() {
        return displayName;
    }
}
