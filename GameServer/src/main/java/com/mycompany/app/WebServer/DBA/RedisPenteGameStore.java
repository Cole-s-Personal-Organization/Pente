package com.mycompany.app.WebServer.DBA;

import java.awt.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mycompany.app.Game.Pente.PenteBoardIdentifierEnum;
import com.mycompany.app.Game.Pente.PenteGameModel;
import com.mycompany.app.Game.Pente.PenteGameSettings;
import com.mycompany.app.Game.Pente.PenteTurn;
import com.mycompany.app.WebServer.UuidValidator;
import com.mycompany.app.WebServer.Models.GameServerInfo;
import com.mycompany.app.WebServer.Models.GameServerInfo.GameRunState;

import jakarta.json.Json;
import redis.clients.jedis.Jedis;

public class RedisPenteGameStore {

    private static final String GAME_PREFIX = "penteGame:";
    private static final String GAME_HEADERS_PREFIX = "penteGameHeaders";

    private static ObjectMapper mapper = new ObjectMapper();

    public static Set<GameServerInfo> getPenteGameHeaders(Jedis jedis) {
        Set<GameServerInfo> headers = new HashSet<>();
        Set<String> stringifiedGameHeaders = jedis.smembers(GAME_HEADERS_PREFIX);

        try {
            for (String headerStr : stringifiedGameHeaders) {
                JsonNode jsonHeader = mapper.readTree(headerStr);

                UUID gameId = null;
                String lobbyName = null;
                UUID gameCreator = null;
                String timeCreatedAt = null;
                GameRunState runState = null;

                JsonNode gameIdNode = jsonHeader.get("gameId");
                JsonNode lobbyNameNode = jsonHeader.get("lobbyName");
                JsonNode gameCreatorIdNode = jsonHeader.get("gameCreator");
                JsonNode timeCreatedAtNode = jsonHeader.get("timeCreatedAt");
                JsonNode runStateNode = jsonHeader.get("runState");

                if (gameIdNode != null && gameIdNode.isTextual()) {
                    gameId = UUID.fromString(gameIdNode.asText());
                } 
                if (lobbyNameNode != null && lobbyNameNode.isTextual()) {
                    lobbyName = lobbyNameNode.asText();
                } 
                if (gameCreatorIdNode != null && gameCreatorIdNode.isTextual() && UuidValidator.isValidUUID(gameCreatorIdNode.asText())) {
                    gameCreator = UUID.fromString(gameCreatorIdNode.asText());
                } 
                if (timeCreatedAtNode != null && timeCreatedAtNode.isTextual()) {
                    timeCreatedAt = timeCreatedAtNode.asText();
                } 
                if (runStateNode != null && runStateNode.isTextual()) {
                    runState = GameRunState.valueOf(runStateNode.asText());
                } 

                if (gameId != null && lobbyName != null && gameCreator != null && timeCreatedAt != null && runState != null) {
                    headers.add(new GameServerInfo(
                    gameId,
                    lobbyName,
                    gameCreator,
                    timeCreatedAt,
                    runState
                ));
                }
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return headers;
    }

    public static GameServerInfo getPenteGameHeaderByGameId(Jedis jedis, UUID gameId) {
        String gameIdAsString = gameId.toString();
        GameServerInfo header = null;
        try {
            String hashKey = GAME_PREFIX + gameIdAsString;
            String stringifiedGameHeader = jedis.hget(hashKey, "header");
            JsonNode jsonHeader = mapper.readTree(stringifiedGameHeader);

            String lobbyName = null;
            UUID gameCreator = null;
            String timeCreatedAt = null;
            GameRunState runState = null;

            JsonNode gameIdNode = jsonHeader.get("gameId");
            JsonNode lobbyNameNode = jsonHeader.get("lobbyName");
            JsonNode gameCreatorIdNode = jsonHeader.get("gameCreator");
            JsonNode timeCreatedAtNode = jsonHeader.get("timeCreatedAt");
            JsonNode runStateNode = jsonHeader.get("runState");

            if (gameIdNode != null && gameIdNode.isTextual()) {
                gameId = UUID.fromString(gameIdNode.asText());
            }
            if (lobbyNameNode != null && lobbyNameNode.isTextual()) {
                lobbyName = lobbyNameNode.asText();
            }
            if (gameCreatorIdNode != null && gameCreatorIdNode.isTextual() && UuidValidator.isValidUUID(gameCreatorIdNode.asText())) {
                gameCreator = UUID.fromString(gameCreatorIdNode.asText());
            }
            if (timeCreatedAtNode != null && timeCreatedAtNode.isTextual()) {
                timeCreatedAt = timeCreatedAtNode.asText();
            }
            if (runStateNode != null && runStateNode.isTextual()) {
                runState = GameRunState.valueOf(runStateNode.asText());
            }

            if (gameId != null && lobbyName != null && gameCreator != null && timeCreatedAt != null && runState != null) {
                header = new GameServerInfo(
                gameId,
                lobbyName,
                gameCreator,
                timeCreatedAt,
                runState
                );
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return header;
    }

    // public static void getPenteGameHeaderByGameId(Jedis jedis, UUID gameId) {
    //     String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();
    // }

    public static void createPenteGame(Jedis jedis, GameServerInfo header, PenteGameModel game) {
        try {
            UUID gameId = header.getGameId();
            String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();
            System.out.println("Hash key set to: " + SPECIFIED_GAME_PREFIX);

            // insert header
            String serializedHeader = mapper.writeValueAsString(header);
            jedis.sadd(GAME_HEADERS_PREFIX, serializedHeader);

            // insert game detail hash 
            jedis.hset(SPECIFIED_GAME_PREFIX, "header", serializedHeader);

            // insert game detail hash 
            String serializedPlayerIdList = "[]";
            jedis.hset(SPECIFIED_GAME_PREFIX, "players", serializedPlayerIdList);

            // insert game settings 
            String serializedGameSettings = mapper.writeValueAsString(game.getGameSettings());
            jedis.hset(SPECIFIED_GAME_PREFIX, "settings", serializedGameSettings);

            // insert player captures
            String serializedPlayerCaptures = mapper.writeValueAsString(game.getPlayerCaptures());
            jedis.hset(SPECIFIED_GAME_PREFIX, "playerCaptures", serializedPlayerCaptures);

            // insert board state
            String serializedBoardState = mapper.writeValueAsString(game.getGameBoard());
            jedis.hset(SPECIFIED_GAME_PREFIX, "currentBoard", serializedBoardState);

            System.out.println(String.join(
                "Hash Created: {\n",
                "    field \"header\": " + serializedHeader + "\n",
                "    field \"players\": " + serializedPlayerIdList + "\n",
                "    field \"settings\": " + serializedGameSettings + "\n",
                "    field \"playerCaptures\": " + serializedPlayerCaptures + "\n",
                "    field \"currentBoard\": " + serializedBoardState + "\n"
            ));
        } catch (Exception e) {
            // TODO: handle exception
        }
    } 

    public static ArrayList<UUID> getPlayersInGame(Jedis jedis, UUID gameId) {
        String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();

        String serializedPlayerList = jedis.hget(SPECIFIED_GAME_PREFIX, "players");

        try {
            if (serializedPlayerList != null) {
                ArrayList<String> playerStrIdList = mapper.readValue(serializedPlayerList, new TypeReference<ArrayList<String>>() {});
                
                ArrayList<UUID> playerIdList = new ArrayList<>(playerStrIdList.stream()
                                                                .map(UUID::fromString)
                                                                .collect(Collectors.toList()));
                return playerIdList;
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return new ArrayList<>();
    }

    public static void addPlayerToGame(Jedis jedis, UUID gameId, UUID playerId) {
        String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();

        ArrayList<UUID> currPlayers = getPlayersInGame(jedis, gameId);
        currPlayers.add(playerId);

        try {
            String serializedPlayerList = mapper.writeValueAsString(currPlayers);

            jedis.hset(SPECIFIED_GAME_PREFIX, "players", serializedPlayerList);

            // add player captures counter 
            // ArrayList<Integer> playerCaptures = getPlayerCaptures(jedis, gameId);
            // playerCaptures.add(0);
            // String serializedPlayerCaptures = mapper.writeValueAsString(playerCaptures);
            // jedis.hset(SPECIFIED_GAME_PREFIX, "playerCaptures", serializedPlayerCaptures);

        } catch (JsonProcessingException e) {
            // TODO: handle exception
        }
    }

    public static void removePlayerFromGame(Jedis jedis, UUID gameId, UUID playerId) {
        String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();

        ArrayList<UUID> currPlayers = getPlayersInGame(jedis, gameId);

        try {
            if (currPlayers.contains(playerId)) {
                currPlayers.remove(playerId);

                String serializedPlayerList = mapper.writeValueAsString(currPlayers);

                jedis.hset(SPECIFIED_GAME_PREFIX, "players", serializedPlayerList);
            }
        } catch (JsonProcessingException e) {
            // TODO: handle exception
        }
    }

    public static PenteGameSettings getGameSettingsByGameId(Jedis jedis, UUID gameId) {
        String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();

        String serializedSettings = jedis.hget(SPECIFIED_GAME_PREFIX, "settings");

        try {
            if (serializedSettings != null) {
                PenteGameSettings settings = mapper.readValue(serializedSettings, new TypeReference<PenteGameSettings>() {});
                
                return settings;
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return null;
    }

    /**
     * Get player captures for a given game.
     * @param jedis
     * @param gameId
     */
    public static ArrayList<Integer> getPlayerCaptures(Jedis jedis, UUID gameId) {
        String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();
        
        String serializedPlayerCaptures = jedis.hget(SPECIFIED_GAME_PREFIX, "playerCaptures");

        try {
            if (serializedPlayerCaptures != null) {
                ArrayList<Integer> playerCaptures = mapper.readValue(serializedPlayerCaptures, new TypeReference<ArrayList<Integer>>() {});
                
                return playerCaptures;
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return new ArrayList<>();
    }

    public static void addPlayerCaptureByPlayerId(Jedis jedis, UUID gameId, UUID playerId) {
        String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();

        ArrayList<Integer> captures = getPlayerCaptures(jedis, gameId);

        // use ordering of player id's to deterimine which index to iterate
        ArrayList<UUID> playerIds = getPlayersInGame(jedis, gameId);
        int playerIndex = playerIds.indexOf(playerId);

        if (playerIndex >= captures.size()) {
            // error 
        }
        int currVal = captures.get(playerIndex);
        captures.set(playerIndex, ++currVal);

        try {
            String serializedPlayerCaptures = mapper.writeValueAsString(captures);
            jedis.hset(SPECIFIED_GAME_PREFIX, "playerCaptures", serializedPlayerCaptures);
        } catch (JsonProcessingException e) {
            // TODO: handle exception
        }
    }

    public static Integer[][] getBoardStateByGameId(Jedis jedis, UUID gameId) {
        String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();
        
        String serializedCurrBoardState = jedis.hget(SPECIFIED_GAME_PREFIX, "currentBoard");

        try {
            if (serializedCurrBoardState != null) {
                Integer[][] boardState = mapper.readValue(serializedCurrBoardState, new TypeReference<Integer[][]>() {});
                
                return boardState;
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return null;
    }   

    public static void setMoveToBoardStateByGameId(Jedis jedis, UUID gameId, PenteTurn turn) {
        // String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();

        // // inflate a model object
        // Integer[][] currBoardState = getBoardStateByGameId(jedis, gameId);
        // PenteGameBoardModel boardModel = new PenteGameBoardModel(currBoardState);

        // try {
        //     boardModel.setMove(turn);

        //     String serializedPlayerCaptures = mapper.writeValueAsString(boardModel.getGameBoard());
        //     jedis.hset(SPECIFIED_GAME_PREFIX, "currentBoard", serializedPlayerCaptures);

            
        //     boardModel.
        // } catch (InvalidTurnException e) {
        //     // TODO: handle exception
        // } catch (JsonProcessingException e) {

        // }

        // update board, player captures

    }

    public static PenteBoardIdentifierEnum getPlayerGameNum(Jedis jedis, UUID gameId, UUID playerId) {
        // get player number via player list index 
        ArrayList<UUID> playerIds = getPlayersInGame(jedis, gameId);
        int playerNumberEnumIndex = playerIds.indexOf(playerId) + 1; // player number = index + 1

        if (PenteBoardIdentifierEnum.values().length > playerNumberEnumIndex) {
            return null;
        }
        return PenteBoardIdentifierEnum.values()[playerNumberEnumIndex];
    }
 


    public static void addToPenteGameLog(Jedis jedis, UUID gameId, String log) {
        
    }

}
