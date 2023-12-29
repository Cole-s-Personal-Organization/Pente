package com.mycompany.app.WebServer;

public class User {
    private String userId;
    private String displayName;

    public User(String uid, String displayName) {
        this.userId = uid;
        this.displayName = displayName; 
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
