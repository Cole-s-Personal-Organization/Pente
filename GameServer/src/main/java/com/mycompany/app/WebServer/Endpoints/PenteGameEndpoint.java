package com.mycompany.app.WebServer.Endpoints;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import com.mycompany.app.Game.Pente.PenteGameSettings;
import com.mycompany.app.WebServer.UuidValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A Servlet for handling all requests to /pente-game/*
 * @author cole
 * 
 * Comprehensive list of endpoints:
 * <p><ul>
 * <li> GET gameserver/pente-game/list/head - list of all current game's header info
 * <li> GET gameserver/pente-game/{game-id}/head - get game's header info corresponding to game id
 * <li> GET gameserver/pente-game/{game-id}/board - get game's board state corresponding to game id
 * 
 * <li> POST gameserver/pente-game/create - create a new pente game 
 *          - creator-id (UUID)
 *          - timestamp (time) 
 * <li> POST gameserver/pente-game/settings - adjust the settings of a game
 *          - game-id (UUID)
 *          - settings (Obj): {
 *              numInARowToWin (int),
 *              capturesToWin (int)} 
 * <li> POST gameserver/pente-game/start - create a new pente game 
 *          - game-id (UUID)
 *          - timestamp (time) 
 * <li> POST gameserver/pente-game/move - post a move 
 *          - game-id (UUID)
 *          - player-id (UUID)
 *          - timestamp (time)
 *          - move (Obj): {
 *              xPos (int),
 *              yPos (int)}
 * <li> POST gameserver/pente-game/leave - have a player leave a game, ends long running SSE stream
 *          - game-id (UUID)
 *          - player-id (UUID)
 *          - timestamp (time)
 * <li> POST gameserver/pente-game/join - have a player join a game, establishes an long running SSE stream 
 *          - game-id (UUID)
 *          - player-id (UUID)
 *          - timestamp (time)
 * </ul><p>
 */
@WebServlet("/pente-game/*")
public class PenteGameEndpoint extends HttpServlet {

    // --------------------------------------------------------------------------------
    //      GET Requests
    // --------------------------------------------------------------------------------

    /**
     * list of all current game's header info
     * e.g. GET gameserver/pente-game/list/head
     * @return json string of listed game headers 
     */
    private String handleGetListGameHeaders()  {
        return "";
    }

    /**
     * get game's header info corresponding to game id
     * e.g. GET gameserver/pente-game/{game-id}/head
     * @param gameId
     * @return json string of game header
     */
    private String handleGetGameHeaderByGameId(UUID gameId) {
        return "";
    }

    /**
     * get game's board state corresponding to game id
     * e.g. GET gameserver/pente-game/{game-id}/board
     * @param gameId
     * @return json representation of board state
     */
    private String handleGetGameStateByGameId(UUID gameId) {
        return "";
    }

    // --------------------------------------------------------------------------------
    //      POST Requests
    // --------------------------------------------------------------------------------

    /**
     * create a new pente game 
     * e.g. POST gameserver/pente-game/create
     * @param creatorId
     * @param timestamp
     */
    private void handlePostCreateGame(UUID creatorId, Date timestamp) {

    }

    /**
     * adjust the settings of a game
     * e.g. POST gameserver/pente-game/settings
     * @param gameId
     * @param settings
     */
    private void handlePostGameSettings(UUID gameId, PenteGameSettings settings) {

    }

    /**
     * create a new pente game 
     * e.g. POST gameserver/pente-game/start
     * @param gameId
     * @param timeStamp
     */
    private void handlePostStartGame(UUID gameId, Date timeStamp) {

    } 



    private void handleGetEndpoint(List<String> currEndpointList) {

        switch (currEndpointPathElement) {
            case "/list":
                handleListGames();
                break;

            case "/create":
                handleCreateGame();
                break;

            default:
                if (UuidValidator.isValidUUID(currEndpointPathElement)) {
                    UUID gameId = UUID.fromString(currEndpointPathElement);

                    handleGameSpecificGetEndpoint(
                        gameId,
                        currEndpointList
                    );
                }
                break;
        }
    }

    private void handleGameSpecificGetEndpoint(UUID gameId, List<String> currEndpointList) {
        if (currEndpointList.size() == 0) {
            handleGetGameInfo(gameId);
            return;
        }

        String nextPathElement = currEndpointList.remove(0);
        if ("/board".equals(nextPathElement)) {
            handleGetGameBoard(gameId);
        } else {
            // Handle other cases, if needed
        }
    }

    private void handleListGames() {
        // Implementation for listing available games
        // Example: GET gameserver/pente-game/list/head
    }

    // Stub for creating a new game
    private void handleCreateGame() {
        // Implementation for creating a new game
        // Example: /game/create
    }

    // Stub for getting specific game information
    private void handleGetGameInfo(UUID gameId) {
        // Implementation for getting specific game information
        // Example: GET /game/{game_id}
    }

    // Stub for getting game board state
    private void handleGetGameBoard(UUID gameId) {
        // Implementation for getting game board state
        // Example: /game/{game_id}/board
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo.startsWith("/join")) {
            handleJoinGame();
        } else if (pathInfo.startsWith("/move")) {
            handleMakeMove();
        } else if (pathInfo.startsWith("/leave")) {
            handleLeaveGame();
        }
    }

    // Stub for handling joining a game
    private void handleJoinGame() {
        // Implementation for joining a game
        // Example: /game/{gameId}/join
    }

    // Stub for handling making a move
    private void handleMakeMove() {
        // Implementation for making a move
        // Example: /game/{gameId}/move
    }

    // Stub for handling leaving a game
    private void handleLeaveGame() {
        // Implementation for leaving a game
        // Example: /game/{gameId}/leave
    }
}

