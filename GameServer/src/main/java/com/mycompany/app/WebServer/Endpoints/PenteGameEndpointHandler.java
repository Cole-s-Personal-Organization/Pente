package com.mycompany.app.WebServer.Endpoints;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.Game.Pente.PenteBoardIdentifierEnum;
import com.mycompany.app.Game.Pente.PenteGameModel;
import com.mycompany.app.Game.Pente.PenteGameSettings;
import com.mycompany.app.Game.Pente.PenteTurn;
import com.mycompany.app.WebServer.RedisBackedCache;
import com.mycompany.app.WebServer.RedisConnectionManager;
import com.mycompany.app.WebServer.UuidValidator;
import com.mycompany.app.WebServer.DBO.RedisPenteGameStore;
import com.mycompany.app.WebServer.Models.GameServerInfo;
import com.mycompany.app.WebServer.Models.GameServerInfo.GameRunState;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
 *          - lobbyName (String)
 *          - timestamp (time) 
 * <li> POST gameserver/pente-game/settings - adjust the settings of a game
 *          - game-id (UUID)
 *          - settings (Obj): {
 *              numInARowToWin (int),
 *              capturesToWin (int)} 
 * <li> POST gameserver/pente-game/start - start a new pente game 
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
public class PenteGameEndpointHandler extends HttpServlet {

    /**
     * Used by endpoint routing functions to denote how a http message path has be parsed
     */
    public enum EndpointRouterResponseId {
        // errors
        missingEndpointError,

        // get endpoints 
        getlistGameHeaders,
        getSpecificGameHeader,
        getSpecificGameBoard,
        getConnectionToGame,

        // post endpoints
        postCreateGame,
        postGameSettings,
        postGameStart,
        postGameMove,
        postLeaveGame,
        postJoinGame
    }



    // public Jedis cache;
    private static final ObjectMapper mapper = new ObjectMapper();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public PenteGameEndpointHandler() {
    }

    // private static Jedis connectJedisWithRetry() {
    //     int retriesAttempted = 0;

    //     while (retriesAttempted < MAX_RETRIES) {
    //         try {
    //             Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);
    //             System.out.println("Ping result: " + jedis.ping());
    //             // Test the connection by sending a ping
    //             if ("PONG".equalsIgnoreCase(jedis.ping())) {
    //                 return jedis;
    //             }
    //         } catch (Exception e) {
    //             System.err.println("Error connecting to Redis. Retrying in " + RETRY_DELAY_MS + " ms.");
    //             // Log or handle the exception as needed
    //         }

    //         retriesAttempted++;
    //         try {
    //             Thread.sleep(RETRY_DELAY_MS);
    //         } catch (InterruptedException e) {
    //             Thread.currentThread().interrupt();
    //         }
    //     }

    //     System.err.println("Max retries reached. Unable to connect to Redis.");
    //     return null;
    // }


    /**
     * Converts a generic incoming post request body json string into a jsonNode object.
     * @param req request object
     * @return a jsonified post request content object 
     */
    private JsonNode getPostRequestBody(HttpServletRequest req) {
        JsonNode postDataJsonNode = null;
        try {
            BufferedReader reader = req.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            System.out.println("Post Data: " + requestBody.toString());

            // Process the data
            String postData = requestBody.toString();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(postData);
        } catch (IOException e) {
            System.out.println("Warning: bad parse of request");
        }
        return postDataJsonNode;
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

        if (cacheManager == null) {
            System.err.println("Missing cache manager connection pool");
            return;
        }

        try (Jedis jedis = cacheManager.getJedisPool().getResource()) {
            Set<GameServerInfo> gameHeaders = RedisPenteGameStore.getPenteGameHeaders(jedis);
            Set<String> serializedGameHeaders = new HashSet<>();

            try {
                for (GameServerInfo gameHeader : gameHeaders) {
                    serializedGameHeaders.add(mapper.writeValueAsString(gameHeader));
                }
                // return "";
            } catch (JsonProcessingException e) {
                // return buildInternalServerProcessingError(); 
                e.printStackTrace();
            }

            try {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setHeader("Location", "/gameserver/pente-game/list/head");
                resp.setContentType("application/json");
                resp.getWriter().write("{\"gameHeaders\": " + serializedGameHeaders + "}");
            } catch (IOException e) {
                // TODO: handle exception
                System.err.println("Couldn't respond to request due to IOException");
                e.printStackTrace();
            }
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

    private String handleGetGameConnection(HttpServletRequest req, HttpServletResponse resp, UUID gameId) {
        resp.setContentType("text/event-stream");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");

        try {
            final PrintWriter writer = resp.getWriter();

            // Send a dummy event every second for demonstration purposes
            scheduler.scheduleAtFixedRate(() -> {
                writer.write("data: {\"message\": \"This is a server-sent event.\"}\n\n");
                writer.flush();
            }, 0, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            // TODO: handle exception
        }
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

            JsonNode postDataContent = this.getPostRequestBody(req);

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
                resp.getWriter().write("{\"message\": \"Resource created successfully\"}");
            } catch (IOException e) {
                // TODO: handle exception
                System.err.println("Couldn't respond to request due to IOException");
                e.printStackTrace();
            }

        }
        
        // // write new game data to redis 
        // try (Jedis jedisInst = new Jedis("localhost", 6379)) {
        //     ObjectMapper mapper = new ObjectMapper();
        //     String jsonStringGameModel = mapper.writer().writeValueAsString(gameModel);

        //     jedisInst.hset("pente-game", gameUuid.toString(), jsonStringGameModel);
        // } catch (Exception e) {
        //     // TODO: handle exception
        //     System.out.println("error");
        //     return;
        // }

        // return build201SuccessfulPostMessage();
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
     * start a new pente game 
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
        // playing a move will post move data to the server 

        // use sse's to stream game changes back to the player 
        // once it is the players turn again end stream
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
        System.out.println("First element: " + firstPathElement);

        if (UuidValidator.isValidUUID(firstPathElement)) {
            System.out.println("First element Id match");
            if (pathInfo.size() == 1) {
                // prevent GET gameserver/pente-game/{game-id}
                return EndpointRouterResponseId.missingEndpointError;
            }

            String secondPathElement = pathInfo.get(1);
            switch (secondPathElement) {
                case "head":
                    return EndpointRouterResponseId.getSpecificGameHeader;
                case "board":
                    return EndpointRouterResponseId.getSpecificGameBoard;
                default:
                    return EndpointRouterResponseId.missingEndpointError;
            }
        }
        else if (firstPathElement.equals("list")) {
            if (pathInfo.size() == 1) {
                // prevent GET gameserver/pente-game/list
                return EndpointRouterResponseId.missingEndpointError;
            }

            String secondPathElement = pathInfo.get(1);
            System.out.println("Second element " + secondPathElement);
            switch (secondPathElement) {
                case "head":
                    return EndpointRouterResponseId.getlistGameHeaders;
            
                default:
                    return EndpointRouterResponseId.missingEndpointError;
            }
        }

        // prevent non-understood endpoint path
        System.out.println("First element not understood");
        return EndpointRouterResponseId.missingEndpointError;
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
                    return EndpointRouterResponseId.postGameSettings;
                case "start": 
                    return EndpointRouterResponseId.postGameStart;
                case "move":
                    return EndpointRouterResponseId.postGameMove;         
                case "leave":
                    return EndpointRouterResponseId.postLeaveGame;
                case "join":
                    return EndpointRouterResponseId.postJoinGame;
                default:
                    return EndpointRouterResponseId.missingEndpointError;
            }
        } 
        else if (firstPathElement == "create") {
            return EndpointRouterResponseId.postCreateGame;
        }

        // prevent non-understood endpoint path
        return EndpointRouterResponseId.missingEndpointError;
    }
    

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Get endpoint hit");
        String jsonStringResponse = "";
        String[] pathInfoList = req.getPathInfo().split("/");
        ArrayList<String> pathInfoArrayList = new ArrayList<>(Arrays.asList(pathInfoList).subList(1, pathInfoList.length));

        System.out.println("Request recieved: " + pathInfoArrayList.toString());

        // if(cache == null) {
        //     resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        //     resp.getWriter().println("Service Unavailable: Backend service unable to connect to Redis");
        //     return;
        // }


        EndpointRouterResponseId endpointResponseId = routeGetEndpoints(pathInfoArrayList);
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

            case getSpecificGameHeader: // GET gameserver/pente-game/{game-id}/head
                jsonStringResponse = handleGetGameHeaderByGameId(
                    UUID.fromString(pathInfoArrayList.get(1))
                );
                break;

            case getSpecificGameBoard: // GET gameserver/pente-game/{game-id}/board 
                jsonStringResponse = handleGetGameStateByGameId(
                    UUID.fromString(pathInfoArrayList.get(1))
                );
                break;

            case getlistGameHeaders: // GET gameserver/pente-game/list/head
                handleGetListGameHeaders(req, resp);
                break;

            case getConnectionToGame: // GET gameserver/pente-game/{game-id}/sse-connection
                jsonStringResponse = handleGetGameConnection(req, resp, UUID.randomUUID());
                break;
        
            default:
                break;
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //   POST gameserver/pente-game/create - create a new pente game 
        //   POST gameserver/pente-game/settings - adjust the settings of a game
        //   POST gameserver/pente-game/start - start a new pente game 
        //   POST gameserver/pente-game/move - post a move 
        //   POST gameserver/pente-game/leave - have a player leave a game, ends long running SSE stream
        //   POST gameserver/pente-game/join - have a player join a game, establishes an long running SSE stream 
        String[] pathInfoList = req.getPathInfo().split("/");
        ArrayList<String> pathInfoArrayList = new ArrayList<>(Arrays.asList(pathInfoList).subList(1, pathInfoList.length));

        System.out.println("Request recieved: " + pathInfoArrayList.toString());

        // Jedis cache = connectJedisWithRetry();
        // if(cache == null) {
        //     resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        //     resp.getWriter().println("Service Unavailable: Backend service unable to connect to Redis");
        //     return;
        // }

        EndpointRouterResponseId endpointResponseId = routePostEndpoints(pathInfoArrayList);
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

            // case postGameStart: // POST gameserver/pente-game/start
            //     jsonStringResponse = handlePostStartGame(null, null);
            //     break;
            
            // case postGameMove: // POST gameserver/pente-game/move
            //     jsonStringResponse = handlePostGameMove(null, null, null);
            //     break;
            
            // case postJoinGame: // POST gameserver/pente-game/join
            //     jsonStringResponse = handlePostJoinGame();
            //     break;
            // case postLeaveGame: // POST gameserver/pente-game/leave
            //     jsonStringResponse = handlePostLeaveGame(null, null, null);
            //     break;

            default:
                break;
        }
    }

    @Override
    public void destroy() {
        scheduler.shutdown();
    }
}

