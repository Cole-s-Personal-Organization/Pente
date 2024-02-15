package com.mycompany.app.WebServer.Models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Contains abstract information about an induvidual game as it relates to the server
 * @author Cole
 */
public class GameServerInfo {

    /**
     * An enum used to describe the current state of a game
     */
    public enum GameRunState {
        Created, Running, Ended
    }

    private UUID gameId;
    private String lobbyName;
    private UUID gameHost; 
    private String timeCreatedAt;
    private GameRunState runState;

    public GameServerInfo(UUID gameId, String lobbyName, UUID gameHost, String timeCreatedAt, GameRunState runState) {
        this.gameId = gameId;
        this.lobbyName = lobbyName;
        this.gameHost = gameHost;
        this.timeCreatedAt = timeCreatedAt;
        this.runState = runState;
    }
    public UUID getGameId() {
        return gameId;
    }
    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
    public String getLobbyName() {
        return lobbyName;
    }
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }
    public UUID getGameHost() {
        return gameHost;
    }
    public void setGameHost(UUID gameHost) {
        this.gameHost = gameHost;
    }
    public String getTimeCreatedAt() {
        return timeCreatedAt;
    }
    public void setTimeCreatedAt(String timeCreatedAt) {
        this.timeCreatedAt = timeCreatedAt;
    }
    public GameRunState getRunState() {
        return runState;
    }
    public void setRunState(GameRunState runState) {
        this.runState = runState;
    }
}
