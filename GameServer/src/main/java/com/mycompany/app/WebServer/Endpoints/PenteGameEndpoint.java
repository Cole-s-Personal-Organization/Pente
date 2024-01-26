package com.mycompany.app.WebServer.Endpoints;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.Game.Pente.PenteBoardIdentifierEnum;
import com.mycompany.app.Game.Pente.PenteGameSettings;
import com.mycompany.app.Game.Pente.PenteTurn;
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
        try {
            // Create a sample Java object
            PenteTurn sampleObject = new PenteTurn.PenteTurnBuilder(0, 25, PenteBoardIdentifierEnum.PLAYER1).build();

            // Convert the Java object to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(sampleObject);
            return jsonString;
        } catch (JsonProcessingException e) {
            return buildInternalServerProcessingError();
        }
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

    //     <li> POST gameserver/pente-game/move - post a move 
    //  *          - game-id (UUID)
    //  *          - player-id (UUID)
    //  *          - timestamp (time)
    //  *          - move (Obj): {
    //  *              xPos (int),
    //  *              yPos (int)}
    /**
     * post a move to a started game
     * e.g. POST gameserver/pente-game/move 
     * @param gameId
     * @param timestamp
     * @param move
     */
    private void handlePostGameMove(UUID gameId, Date timestamp, PenteTurn move) {

    } 

    /**
     * have a player leave a game, ends long running SSE stream
     * e.g. POST gameserver/pente-game/leave
     * @param gameId
     * @param playerId
     * @param timestamp
     */
    private void handlePostLeaveGame(UUID gameId, UUID playerId, Date timestamp) {

    }

    /**
     * have a player join a game, establishes an long running SSE stream 
     * e.g. POST gameserver/pente-game/join
     * @param gameId
     * @param playerId
     * @param timestamp
     */
    private void handlePostJoinGame(UUID gameId, UUID playerId, Date timestamp) {

    }


    /**
     * Helper function used to construct a 404 json response.
     * @return A json string representation of a 404 error
     */
    private String buildEndpointNotFoundError() {
        try {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Endpoint not found");
            errorResponse.put("status", 404);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(errorResponse);

            return jsonString;
        } catch (JsonProcessingException e) {
            System.err.println("Error processing endpoint not found error");
            return buildInternalServerProcessingError();
        }
    }

    /**
     * Helper function used to construct a 500 json response.
     * @return A json string representation of a 500 error
     */
    private String buildInternalServerProcessingError() {
        try {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Endpoint not found");
            errorResponse.put("status", 404);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(errorResponse);

            return jsonString;
        } catch (Exception e) {
            System.err.println("The Error handler error has errored... unfortunate");
            return "";
        }
    }
    

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//         GET gameserver/pente-game/list/head - list of all current game's header info
//  * <li> GET gameserver/pente-game/{game-id}/head - get game's header info corresponding to game id
//  * <li> GET gameserver/pente-game/{game-id}/board - get game's board state corresponding to game id
        String jsonStringResponse = "";
        String[] pathInfoList = req.getPathInfo().split("/");
        ArrayList<String> pathInfoArrayList = new ArrayList<>(Arrays.asList(pathInfoList).subList(1, pathInfoList.length));

        System.out.println("Request recieved: " + pathInfoArrayList.toString());

        if (pathInfoArrayList.size() == 0) {
            // prevent empty path
            System.out.println("Missing first path element");
            jsonStringResponse = buildEndpointNotFoundError();
            return;
        }
        String firstPathElement = pathInfoArrayList.get(0);

        if (UuidValidator.isValidUUID(firstPathElement)) {
            if (pathInfoArrayList.size() == 1) {
                // prevent GET gameserver/pente-game/{game-id}
                System.out.println("No matching path for " + pathInfoArrayList.toString());
                jsonStringResponse = buildEndpointNotFoundError();
                return;
            }

            String secondPathElement = pathInfoArrayList.get(1);
            UUID gameId = UUID.fromString(firstPathElement);

            switch (secondPathElement) {
                case "head":
                    jsonStringResponse = handleGetGameHeaderByGameId(gameId);
                    break;
                
                case "board":
                    jsonStringResponse = handleGetGameStateByGameId(gameId);
                    break;
            
                default:
                    System.out.println("No matching path for " + pathInfoArrayList.toString());
                    jsonStringResponse = buildEndpointNotFoundError();
                    break;
            }
        }
        else if (firstPathElement.equals("list")) {
            if (pathInfoArrayList.size() == 1) {
                // prevent GET gameserver/pente-game/list
                System.out.println("No matching path for " + pathInfoArrayList.toString());
                jsonStringResponse = buildEndpointNotFoundError();
                return;
            }

            String secondPathElement = pathInfoArrayList.get(1);

            switch (secondPathElement) {
                case "head":
                    jsonStringResponse = handleGetListGameHeaders();
                    break;
            
                default:
                    System.out.println("No matching path for " + pathInfoArrayList.toString());
                    jsonStringResponse = buildEndpointNotFoundError();
                    break;
            }
        }
        else {
            // prevent non-understood endpoint path
            System.out.println("No matching path for " + pathInfoArrayList.toString());
            jsonStringResponse = buildEndpointNotFoundError();
            return;
        }
        
        resp.setContentType("application/json");
        resp.getWriter().write(jsonStringResponse);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // if (pathInfo.startsWith("/join")) {
        //     handleJoinGame();
        // } else if (pathInfo.startsWith("/move")) {
        //     handleMakeMove();
        // } else if (pathInfo.startsWith("/leave")) {
        //     handleLeaveGame();
        // }
    }
}

