package com.mycompany.app.WebServer.WebServlets.Pente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.Game.Pente.PenteBoardIdentifierEnum;
import com.mycompany.app.Game.Pente.PenteGameModel;
import com.mycompany.app.Game.Pente.PenteGameSettings;
import com.mycompany.app.Game.Pente.PentePlayerIdentifierEnum;
import com.mycompany.app.Game.Pente.PenteTurn;
import com.mycompany.app.WebServer.RedisConnectionManager;
import com.mycompany.app.WebServer.UuidValidator;
import com.mycompany.app.WebServer.DBA.RedisPenteGameStore;
import com.mycompany.app.WebServer.Models.GameServerInfo;
import com.mycompany.app.WebServer.Models.PenteGameEvent;
import com.mycompany.app.WebServer.Models.GameServerInfo.GameRunState;
import com.mycompany.app.WebServer.WebServlets.EndpointHelperFunctions;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * A Servlet for handling all requests to /pente-game/*
 * @author cole
 * 
 * <br>
 * Comprehensive list of endpoints:
 * <p><ul>
 * <li> GET gameserver/pente-game/list/head - list of all current game's header info
 * <li> GET gameserver/pente-game/{game-id}/head - get game's header info corresponding to game id
 * <li> GET gameserver/pente-game/{game-id}/board - get game's board state corresponding to game id
 * <li> GET gameserver/pente-game/{game-id}/players - get game's information regarding player join status
 * <li> GET gameserver/pente-game/{game-id}/sse-connect - get sse connection to game session
 * <li> GET gameserver/pente-game/{game-id}/turn-counter - get a games turn counter
 * 
 * <li> POST gameserver/pente-game/create - create a new pente game 
 *          - creator-id (UUID)
 *          - lobbyName (String)
 *          - timestamp (time) 
 * <li> POST gameserver/pente-game/{game-id}/settings - adjust the settings of a game
 *          - game-id (UUID)
 *          - settings (Obj): {
 *              numInARowToWin (int),
 *              capturesToWin (int)} 
 * <li> POST gameserver/pente-game/{game-id}/start - start a new pente game 
 *          - game-id (UUID)
 *          - timestamp (time) 
 * <li> POST gameserver/pente-game/{game-id}/move - post a move 
 *          - game-id (UUID)
 *          - player-id (UUID)
 *          - timestamp (time)
 *          - move (Obj): {
 *              xPos (int),
 *              yPos (int)}
 * <li> POST gameserver/pente-game/{game-id}/leave - have a player leave a game, ends long running SSE stream
 *          - game-id (UUID)
 *          - player-id (UUID)
 *          - timestamp (time)
 * <li> POST gameserver/pente-game/{game-id}/join - have a player join a game, establishes an long running SSE stream 
 *          - game-id (UUID)
 *          - player-id (UUID)
 *          - timestamp (time)
 * </ul><p>
 */
@WebServlet("/pente-game/*")
public class PenteGameEndpointHandler extends HttpServlet {

    /**
     * Used by endpoint routing functions to denote how a http message path has be parsed
     */
    public enum EndpointRouterResponseId {
        // errors
        missingEndpointError,

        // get endpoints 
        getlistGameHeaders,
        getSpecificGameHeaderByGameId,
        getSpecificGameBoardByGameId,
        getSSEConnectionToGameByGameId,
        getPlayersInGameByGameId,
        getTurnCounterByGameId,
        getGameStateByGameId,

        // post endpoints
        postCreateGame,
        postGameSettingsByGameId,
        postGameStartByGameId,
        postGameMoveByGameId,
        postLeaveGameByGameId,
        postJoinGameByGameId
    }



    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(PenteGameEndpointHandler.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final String REDIS_BROADCAST_CHANNEL_TEMPLATE = "penteGame:%s:broadcast";


    public PenteGameEndpointHandler() {
    }

    // --------------------------------------------------------------------------------
    //      GET Requests
    // --------------------------------------------------------------------------------

    /**
     * list of all current game's header info
     * redis data shape:
     *      penteGame:<gameId>:header
     * e.g. GET gameserver/pente-game/list/head
     * @return json string of listed game headers 
     */
    private void handleGetListGameHeaders(HttpServletRequest req, HttpServletResponse resp)  {
        ServletContext context = req.getServletContext();
        RedisConnectionManager cacheManager =  (RedisConnectionManager) context.getAttribute("cacheManager");
        Set<String> serializedGameHeaders = new HashSet<>();;

        // abort if null cache
        if (cacheManager == null) {
            System.err.println("Missing cache manager connection pool");
            return;
        }

        // extract data from redis, convert to string for send back to client
        try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
            Set<GameServerInfo> gameHeaders = RedisPenteGameStore.getPenteGameHeaders(jedis);

            try {
                for (GameServerInfo gameHeader : gameHeaders) {
                    serializedGameHeaders.add(mapper.writeValueAsString(gameHeader));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // construct success message return to client
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            // TODO: here for testing purposes, this is bad, must use least access principle
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Location", "/gameserver/pente-game/list/head");
            resp.setContentType("application/json");
            resp.getWriter().write("{\"gameHeaders\": " + serializedGameHeaders + "}");
        } catch (IOException e) {
            // TODO: handle exception
            System.err.println("Couldn't respond to request due to IOException");
            e.printStackTrace();
        }
    }

    /**
     * get game's header info corresponding to game id
     * e.g. GET gameserver/pente-game/{game-id}/head
     * @return json string of game header
     */
    private void handleGetGameHeaderByGameId(HttpServletRequest req, HttpServletResponse resp, UUID gameId) {
        ServletContext context = req.getServletContext();
        RedisConnectionManager cacheManager =  (RedisConnectionManager) context.getAttribute("cacheManager");

        if (cacheManager == null) {
            System.err.println("Missing cache manager connection pool");
            return;
        }

        try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
            GameServerInfo gameHeader = RedisPenteGameStore.getPenteGameHeaderByGameId(jedis, gameId);
            String serializedGameHeader = "";
            try {
                serializedGameHeader = mapper.writeValueAsString(gameHeader);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            try {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setHeader("Location", "gameserver/pente-game/{game-id}/head");
                resp.setContentType("application/json");
                resp.getWriter().write("{\"gameHeader\": " + serializedGameHeader + "}");
            } catch (IOException e) {
                // TODO: handle exception
                System.err.println("Couldn't respond to request due to IOException");
                e.printStackTrace();
            }
        }
    }

    /**
     * get game's board state, capture count, turn counter, and winner value corresponding to game id
     * e.g. GET gameserver/pente-game/{game-id}/board
     * @param gameId
     * @return json representation of board state
     */
    private void handleGetGameStateByGameId(HttpServletRequest req, HttpServletResponse resp, UUID gameId) {
        ServletContext context = req.getServletContext();
        RedisConnectionManager cacheManager =  (RedisConnectionManager) context.getAttribute("cacheManager");

        if (cacheManager == null) {
            System.err.println("Missing cache manager connection pool");
            return;
        }

        try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
            PenteBoardIdentifierEnum[][] board = RedisPenteGameStore.getBoardStateByGameId(jedis, gameId);
            ArrayList<Integer> captures = RedisPenteGameStore.getPlayerCaptures(jedis, gameId);
            PentePlayerIdentifierEnum winner = RedisPenteGameStore.getGameWinner(jedis, gameId);
            Integer turnCounter = RedisPenteGameStore.getGameTurnCounter(jedis, gameId);

            String serializedBoard = "";
            String serializedCaptures = "";
            String serializedWinner = "";
            String serializedTurnCounter = "";
            try {
                serializedBoard = mapper.writeValueAsString(board);
                serializedCaptures = mapper.writeValueAsString(captures);
                serializedWinner = mapper.writeValueAsString(winner);
                serializedTurnCounter = mapper.writeValueAsString(turnCounter);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            try {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setHeader("Location", "gameserver/pente-game/{game-id}/head");
                resp.setContentType("application/json");
                resp.getWriter().write("{\"board\": " + serializedBoard + ", \"captures\": " + serializedCaptures + ", \"winner\": " + serializedWinner + ", \"turnCounter\": " + serializedWinner + "}");
            } catch (IOException e) {
                // TODO: handle exception
                System.err.println("Couldn't respond to request due to IOException");
                e.printStackTrace();
            }
        }
    }

    /**
     * Get game's information regarding player join status
     * e.g. GET gameserver/pente-game/{game-id}/players
     * @param req
     * @param resp
     * @param gameId
     */
    private void handleGetPlayersInGameByGameId(HttpServletRequest req, HttpServletResponse resp, UUID gameId) {
        ServletContext context = req.getServletContext();
        RedisConnectionManager cacheManager =  (RedisConnectionManager) context.getAttribute("cacheManager");
        Set<String> serializedPlayerIds = new HashSet<>();;

        // abort if null cache
        if (cacheManager == null) {
            System.err.println("Missing cache manager connection pool");
            return;
        }

        // extract data from redis, convert to string for send back to client
        try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
            ArrayList<UUID> playerIds = RedisPenteGameStore.getPlayersInGame(jedis, gameId);

            try {
                for (UUID playerId : playerIds) {
                    serializedPlayerIds.add(mapper.writeValueAsString(playerId));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // construct success message return to client
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("Location", "/gameserver/pente-game/{game-id}/players");
            resp.setContentType("application/json");
            resp.getWriter().write("{\"playerIds\": " + serializedPlayerIds + "}");
        } catch (IOException e) {
            // TODO: handle exception
            System.err.println("Couldn't respond to request due to IOException");
            e.printStackTrace();
        }
    } 

    private void handleGetTurnCounter(HttpServletRequest req, HttpServletResponse resp, UUID gameId) {
        ServletContext context = req.getServletContext();
        RedisConnectionManager cacheManager =  (RedisConnectionManager) context.getAttribute("cacheManager");
        String serializedTurnCounter = null;

        // abort if null cache
        if (cacheManager == null) {
            System.err.println("Missing cache manager connection pool");
            return;
        }

        // extract data from redis, convert to string for send back to client
        try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
            Integer turnCounter = RedisPenteGameStore.getGameTurnCounter(jedis, gameId);

            serializedTurnCounter = String.valueOf(turnCounter);
        }

        // construct success message return to client
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("Location", "/gameserver/pente-game/{game-id}/turnCounter");
            resp.setContentType("application/json");
            resp.getWriter().write("{\"turnCounter\": " + serializedTurnCounter + "}");
        } catch (IOException e) {
            System.err.println("Couldn't respond to request due to IOException");
            e.printStackTrace();
        }
    }

    /**
     * Get endpoint used for establishing an sse connection
     * e.g. GET gameserver/pente-game/{game-id}/sse-connect 
     * @param req
     * @param resp
     * @param gameId
     */
    private void handleGetGameConnection(HttpServletRequest req, HttpServletResponse resp, UUID gameId) {
        ServletContext context = req.getServletContext();
        RedisConnectionManager cacheManager =  (RedisConnectionManager) context.getAttribute("cacheManager");
        List<String> events = Collections.synchronizedList(new ArrayList<String>());

        // Set content type to text/event-stream
        resp.setContentType("text/event-stream");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");


        Thread subscriptionThread = new Thread(() -> {
            try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
                String channelName = String.format(REDIS_BROADCAST_CHANNEL_TEMPLATE, gameId.toString());
                System.out.println("Subscribing to channel: " + channelName);
                
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        events.add(message);
                    }
                }, channelName);
            } 
        });
        subscriptionThread.start();
        
        // block endpoint thread to sustain connection
        //   respond to incoming events relevant to game listening to triggered by other endpoint requests
        try (PrintWriter writer = resp.getWriter()) {
            System.out.println("Response writer grabbed.");
            writer.write("connected");
            writer.flush();
            
            while (!Thread.interrupted()) {
                try {
                    if (events.size() > 0) {
                        String eventStr = events.remove(0);
                        PenteGameEvent event = EndpointHelperFunctions.parseGameEvent(eventStr);

                        /*
                         * EVENT MESSAGE FORMAT:
                         *      id: <num>
                         *      event: <event_name>
                         *      data: <json_data>
                         * \n
                         */

                        if (event != null) {
                            writer.write(String.format("id: %s\n", event.getEventId().toString()));
                            writer.write(String.format("event: %s\n", event.getEvent().toString()));
                            writer.write(String.format("data: %s\n", event.getData().asText()));
                            writer.write("\n");
                            writer.flush();
                        }
                    }

                    Thread.sleep(1000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            
            // Close the writer and release resources
            writer.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
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
    private void handlePostCreateGame(HttpServletRequest req, HttpServletResponse resp) {
        ServletContext context = req.getServletContext();
        RedisConnectionManager cacheManager =  (RedisConnectionManager) context.getAttribute("cacheManager");

        if (cacheManager == null) {
            System.err.println("Missing cache manager connection pool");
            return;
        }

        try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
            // parse data from req
            // UUID creatorId, Date timestamp
            String gameName;
            UUID creatorId; 
            LocalDateTime nowTime = LocalDateTime.now();
        
            JsonNode postDataContent = EndpointHelperFunctions.getPostRequestBody(req);
            System.out.println("Post data: " + postDataContent.toString());

            try {
                creatorId = UUID.fromString(postDataContent.get("creatorId").asText());
            } catch (IllegalArgumentException e) {
                System.out.println("Warning: invalid argument uuid passed.");
                send400BadRequest(resp);
                return;
            }

            gameName = postDataContent.get("lobbyName").asText();
            if (gameName == null || gameName == "") {
                System.out.println("Warning: invalid game name passed");
                send400BadRequest(resp);
                return;
            }

            UUID gameUuid = UUID.randomUUID();
            PenteGameModel gameModel = new PenteGameModel();
            GameServerInfo gameHeader = new GameServerInfo(gameUuid, gameName, creatorId, nowTime.toString(), GameRunState.Created);

            RedisPenteGameStore.createPenteGame(jedis, gameHeader, gameModel);
            
            try {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.setHeader("Location", "/gameserver/pente-game/create");
                resp.setContentType("application/json");
                resp.getWriter().write("{\"gameId\": \"" + gameUuid + "\", \"creationTime\": \"" + nowTime.toString() + "\"}");
            } catch (IOException e) {
                // TODO: handle exception
                System.err.println("Couldn't respond to request due to IOException");
                e.printStackTrace();
            }

        }
    }

    /**
     * adjust the settings of a game
     * e.g. POST gameserver/pente-game/settings
     * @param gameId
     * @param settings
     */
    private void handlePostGameSettings(HttpServletRequest req, HttpServletResponse resp, UUID gameId, PenteGameSettings settings) {

    }

    /**
     * start a new pente game 
     * e.g. POST gameserver/pente-game/start
     * @param gameId
     * @param timeStamp
     */
    private void handlePostStartGame(HttpServletRequest req, HttpServletResponse resp, UUID gameId) {
        ServletContext context = req.getServletContext();
        RedisConnectionManager cacheManager =  (RedisConnectionManager) context.getAttribute("cacheManager");

        if (cacheManager == null) {
            System.err.println("Missing cache manager connection pool");
            return;
        }

        try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
            LocalDateTime nowTime = LocalDateTime.now();
        
            JsonNode postDataContent = EndpointHelperFunctions.getPostRequestBody(req);
            System.out.println("Post data: " + postDataContent.toString());


            RedisPenteGameStore.setGameRunState(jedis, gameId, GameRunState.Running);
            GameServerInfo newHeader = RedisPenteGameStore.getPenteGameHeaderByGameId(jedis, gameId);
            String serializedNewGameHeader = "";

            try {
                serializedNewGameHeader = mapper.writeValueAsString(newHeader);
            } catch (JsonProcessingException e) {
                // TODO: handle exception
                System.err.println("Couldn't respond to request due to IOException");
                e.printStackTrace();
            } 

            try {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.setHeader("Location", "/gameserver/pente-game/create");
                resp.setContentType("application/json");
                resp.getWriter().write("{\"gameHeader\": \"" + serializedNewGameHeader + "}");
            } catch (IOException e) {
                // TODO: handle exception
                System.err.println("Couldn't respond to request due to IOException");
                e.printStackTrace();
            }
        }
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
    private void handlePostGameMove(HttpServletRequest req, HttpServletResponse resp, UUID gameId, PenteTurn move) {
        // playing a move will post move data to the server 

        // use sse's to stream game changes back to the player 
        // once it is the players turn again end stream
    } 

    /**
     * have a player leave a game, ends long running SSE stream
     * e.g. POST gameserver/pente-game/leave
     * @param gameId
     * @param playerId
     */
    private void handlePostLeaveGame(HttpServletRequest req, HttpServletResponse resp, UUID gameId, UUID playerId) {

    }

    /**
     * have a player join a game
     * e.g. POST gameserver/pente-game/join
     * @param gameId
     * @param playerId
     */
    private void handlePostJoinGame(HttpServletRequest req, HttpServletResponse resp, UUID gameId) {
        ServletContext context = req.getServletContext();
        RedisConnectionManager cacheManager =  (RedisConnectionManager) context.getAttribute("cacheManager");

        UUID playerId = null;
        LocalDateTime nowTime = null;

        if (cacheManager == null) {
            System.err.println("Missing cache manager connection pool");
            return;
        }

        try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
            GameServerInfo header = RedisPenteGameStore.getPenteGameHeaderByGameId(jedis, gameId);
            
            switch (header.getRunState()) {
                case Created: // game is joinable - pass
                    break;
                case Running:
                    // TODO: for now just send error, in the future allow for game rejoins if allowed
                    send409ConflictError(resp, "Game attempt failed. Game is already Running.");
                    return;
                case Ended: // game is over -> unjoinable
                    send409ConflictError(resp, "Game attempt failed. Game has already Ended.");
                    return;
                default: // game run state doesn't exist return 404 error
                    send404NotFoundError(resp, "Game attempt failed. Game not found.");
                    return;
            }

            // parse data from req
            // UUID creatorId, Date timestamp
            nowTime = LocalDateTime.now();
        
            JsonNode postDataContent = EndpointHelperFunctions.getPostRequestBody(req);
            System.out.println("Post data: " + postDataContent.toString());

            try {
                playerId = UUID.fromString(postDataContent.get("playerId").asText());
            } catch (IllegalArgumentException e) {
                System.out.println("Warning: invalid argument uuid passed.");
                send400BadRequest(resp);
                return;
            }

            RedisPenteGameStore.addPlayerToGame(jedis, gameId, playerId);
            System.out.println("New player resource added to redis");
        } 

        if (playerId == null || nowTime == null) {
            return;
        }
        // from here we know the player has been created successfully
        try (Jedis jedis = cacheManager.getJedisPool().getResource()){
            PenteGameEvent event = new PenteGameEvent(
                UUID.randomUUID(), 
                PenteGameEvent.PenteGameEventType.PlayerJoin, 
                null);
            jedis.publish("penteGame:" + gameId + ":broadcast", event.toJsonString());
            System.out.println("Player join event pushed to pub/sub: " + "penteGame:" + gameId + ":broadcast");
        }

        try {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setHeader("Location", "/gameserver/pente-game/join");
            resp.setContentType("application/json");
            resp.getWriter().write("{\"gameId\": \"" + gameId.toString() + "\", \"timeJoinedAt\": " + nowTime.toString() + "}");
        } catch (IOException e) {
            // TODO: handle exception
            System.err.println("Couldn't respond to request due to IOException");
            e.printStackTrace();
        }
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

    private void send404NotFoundError(HttpServletResponse resp, String errMsg) {
        try {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("application/json");
            try (PrintWriter out = resp.getWriter()) {
                out.println("{\"error\": \"" + errMsg + "\"}");
            }
            return;
        } catch (IOException e) {
            // TODO: handle exception
        }
    }

    private void send409ConflictError(HttpServletResponse resp, String errMsg) {
        try {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.setContentType("application/json");
            try (PrintWriter out = resp.getWriter()) {
                out.println("{\"error\": \"" + errMsg + "\"}");
            }
            return;
        } catch (IOException e) {
            // TODO: handle exception
        }
    }

    private void send400BadRequest(HttpServletResponse resp) {
        try {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            try (PrintWriter out = resp.getWriter()) {
                out.println("{\"error\": \"Missing required arguments: 'name' and 'description'\"}");
            }
            return;
        } catch (IOException e) {
            // TODO: handle exception
        }
    }


    public static EndpointRouterResponseId routeGetEndpoints(ArrayList<String> pathInfo) {
        if (pathInfo.size() == 0) {
            // prevent empty path
            return EndpointRouterResponseId.missingEndpointError;
        }

        String firstPathElement = pathInfo.get(0);

        if (UuidValidator.isValidUUID(firstPathElement)) {
            if (pathInfo.size() == 1 || pathInfo.size() > 2) {
                // prevent GET gameserver/pente-game/{game-id} and GET gameserver/pente-game/{game-id}/*/*
                return EndpointRouterResponseId.missingEndpointError;
            }

            String secondPathElement = pathInfo.get(1);
            switch (secondPathElement) {
                case "head":
                    return EndpointRouterResponseId.getSpecificGameHeaderByGameId;
                case "board":
                    return EndpointRouterResponseId.getSpecificGameBoardByGameId;
                case "players":
                    return EndpointRouterResponseId.getPlayersInGameByGameId;
                case "sse-connect": 
                    return EndpointRouterResponseId.getSSEConnectionToGameByGameId;
                case "turn-counter":
                    return EndpointRouterResponseId.getTurnCounterByGameId;
                case "game-state":
                    return EndpointRouterResponseId.getGameStateByGameId;
                default:
                    return EndpointRouterResponseId.missingEndpointError;
            }
        }
        else if (firstPathElement.equals("list")) {
            if (pathInfo.size() == 1) {
                // prevent GET gameserver/pente-game/list and GET gameserver/pente-game/list/*/*
                return EndpointRouterResponseId.missingEndpointError;
            }

            String secondPathElement = pathInfo.get(1);
            switch (secondPathElement) {
                case "head":
                    return EndpointRouterResponseId.getlistGameHeaders;
            
                default:
                    return EndpointRouterResponseId.missingEndpointError;
            }
        } else {
            // prevent non-understood endpoint path
            System.out.println("First element not understood");
            return EndpointRouterResponseId.missingEndpointError;
        }
    } 

    public static EndpointRouterResponseId routePostEndpoints(ArrayList<String> pathInfo) {
        if (pathInfo.size() == 0) {
            // prevent empty path
            return EndpointRouterResponseId.missingEndpointError;
        }
        String firstPathElement = pathInfo.get(0);

        if (UuidValidator.isValidUUID(firstPathElement)) {
            if (pathInfo.size() == 1) {
                // prevent POST gameserver/pente-game/{game-id}
                return EndpointRouterResponseId.missingEndpointError;
            }

            String secondPathElement = pathInfo.get(1);
            switch (secondPathElement) {
                case "settings":
                    return EndpointRouterResponseId.postGameSettingsByGameId;
                case "start": 
                    return EndpointRouterResponseId.postGameStartByGameId;
                case "move":
                    return EndpointRouterResponseId.postGameMoveByGameId;         
                case "leave":
                    return EndpointRouterResponseId.postLeaveGameByGameId;
                case "join":
                    return EndpointRouterResponseId.postJoinGameByGameId;
                default:
                    return EndpointRouterResponseId.missingEndpointError;
            }
        } 
        else if (firstPathElement.equals("create")) {
            return EndpointRouterResponseId.postCreateGame;
        } else {
            // prevent non-understood endpoint path
            return EndpointRouterResponseId.missingEndpointError;
        }
    }

    private void logRequestDetails(HttpServletRequest request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[REQUEST] - ");
        logMessage.append("Method: ").append(request.getMethod()).append(", ");
        logMessage.append("URL: ").append(request.getRequestURL()).append(", ");
        logMessage.append("Parameters: ").append(request.getQueryString()).append(", ");
        logMessage.append("Headers: ");
        request.getHeaderNames().asIterator().forEachRemaining(headerName ->
                logMessage.append(headerName).append(": ").append(request.getHeader(headerName)).append(", "));
        logMessage.append("Remote Address: ").append(request.getRemoteAddr());
        System.out.println(logMessage);
        // logger.info(logMessage.toString());
    }

    private void logResponseDetails(HttpServletResponse response) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[RESPONSE] - ");
        logMessage.append("Status Code: ").append(response.getStatus()).append(", ");
        logMessage.append("Headers: ");
        for (String headerName : response.getHeaderNames()) {
            logMessage.append(headerName).append(": ").append(response.getHeader(headerName)).append(", ");
        }
        System.out.println(logMessage);
        // logger.info(logMessage.toString());
    }
    

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        logRequestDetails(req);

        String[] pathInfoList = req.getPathInfo().split("/");
        ArrayList<String> pathInfoArrayList = new ArrayList<>(Arrays.asList(pathInfoList).subList(1, pathInfoList.length));
        UUID gameId;

        System.out.println("Request path: " + pathInfoArrayList.toString());

        EndpointRouterResponseId endpointResponseId = routeGetEndpoints(pathInfoArrayList);
    
        if (endpointResponseId != EndpointRouterResponseId.missingEndpointError) {
            System.out.println("Routing to endpoint: \"" + endpointResponseId + "\" (" + req.getPathInfo() + ")");
        }

        switch (endpointResponseId) { 
            case missingEndpointError: // non-matched endpoint
                System.out.println("No matching path for " + pathInfoArrayList.toString());

                try {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.setHeader("Location", "/gameserver/pente-game/list/head");
                    resp.setContentType("application/json");
                    resp.getWriter().write("{\"message\": " + buildEndpointNotFoundError() + "}");
                } catch (IOException e) {
                    // TODO: handle exception
                    System.err.println("Couldn't respond to request due to IOException");
                    e.printStackTrace();
                }
                break;

            case getSpecificGameHeaderByGameId: // GET gameserver/pente-game/{game-id}/head
                gameId = UUID.fromString(pathInfoArrayList.get(0)); 
                handleGetGameHeaderByGameId(req, resp, gameId);
                break;

            case getSpecificGameBoardByGameId: // GET gameserver/pente-game/{game-id}/board 
                // jsonStringResponse = handleGetGameStateByGameId(
                //     UUID.fromString(pathInfoArrayList.get(0))
                // );
                break;

            case getlistGameHeaders: // GET gameserver/pente-game/list/head
                handleGetListGameHeaders(req, resp);
                break;

            case getSSEConnectionToGameByGameId: // GET gameserver/pente-game/{game-id}/sse-connect
                gameId = UUID.fromString(pathInfoArrayList.get(0));
                handleGetGameConnection(req, resp, gameId);
                break;

            case getPlayersInGameByGameId: // GET gameserver/pente-game/{game-id}/players
                gameId = UUID.fromString(pathInfoArrayList.get(0));
                handleGetPlayersInGameByGameId(req, resp, gameId);
                break;

            case getGameStateByGameId: // GET gameserver/pente-game/{game-id}/players
                gameId = UUID.fromString(pathInfoArrayList.get(0));
                handleGetGameStateByGameId(req, resp, gameId);
                break;

            case getTurnCounterByGameId: // GET gameserver/pente-game/{game-id}/players
                gameId = UUID.fromString(pathInfoArrayList.get(0));
                handleGetTurnCounter(req, resp, gameId);
                break;
        
            default:
                break;
        }
        logResponseDetails(resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //   POST gameserver/pente-game/create - create a new pente game 
        //   POST gameserver/pente-game/{game-id}/settings - adjust the settings of a game
        //   POST gameserver/pente-game/{game-id}/start - start a new pente game 
        //   POST gameserver/pente-game/{game-id}/move - post a move 
        //   POST gameserver/pente-game/{game-id}/leave - have a player leave a game, ends long running SSE stream
        //   POST gameserver/pente-game/{game-id}/join - have a player join a game

        // TODO: here for testing purposes, this is bad, must use least access principle
        resp.setHeader("Access-Control-Allow-Origin", "*");

        logRequestDetails(req);
        String[] pathInfoList = req.getPathInfo().split("/");
        ArrayList<String> pathInfoArrayList = new ArrayList<>(Arrays.asList(pathInfoList).subList(1, pathInfoList.length));
        UUID gameId;

        System.out.println("Request path: " + pathInfoArrayList.toString());

        EndpointRouterResponseId endpointResponseId = routePostEndpoints(pathInfoArrayList);
        
        if (endpointResponseId != EndpointRouterResponseId.missingEndpointError) {
            System.out.println("Routing to endpoint: \"" + endpointResponseId + "\" (" + req.getPathInfo() + ")");
        }

        switch (endpointResponseId) { 
            case missingEndpointError: // non-matched endpoint
                System.out.println("No matching path for " + pathInfoArrayList.toString());

                resp.setContentType("application/json");
                resp.getWriter().write(buildEndpointNotFoundError());
                break;

            case postCreateGame: // POST gameserver/pente-game/create
                handlePostCreateGame(req, resp);
                break;
            
            // case postGameSettings: // POST gameserver/pente-game/settings
            //     jsonStringResponse = handlePostGameSettings(null, null);
            //     break;

            case postGameStartByGameId: // POST gameserver/pente-game/start
                gameId = UUID.fromString(pathInfoArrayList.get(0));
                handlePostStartGame(req, resp, gameId);
                break;
            
            // case postGameMove: // POST gameserver/pente-game/move
            //     jsonStringResponse = handlePostGameMove(null, null, null);
            //     break;
            
            case postJoinGameByGameId: // POST gameserver/pente-game/join
                gameId = UUID.fromString(pathInfoArrayList.get(0));
                handlePostJoinGame(req, resp, gameId);
                break;
            // case postLeaveGame: // POST gameserver/pente-game/leave
            //     jsonStringResponse = handlePostLeaveGame(null, null, null);
            //     break;

            default:
                break;
        }
        logResponseDetails(resp);
    }

    @Override
    public void destroy() {
        scheduler.shutdown();
    }
}

