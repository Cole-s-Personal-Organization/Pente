package com.mycompany.app.WebServer.DBO;

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
import com.mycompany.app.Game.Pente.PenteGameModel;
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

    // public static void getPenteGameHeaderByGameId(Jedis jedis, UUID gameId) {
    //     String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();
    // }

    public static void createPenteGame(Jedis jedis, GameServerInfo header, PenteGameModel game) {
        try {
            UUID gameId = header.getGameId();
            String SPECIFIED_GAME_PREFIX = GAME_PREFIX + gameId.toString();

            // insert header
            String serializedHeader = mapper.writeValueAsString(header);
            jedis.sadd(GAME_HEADERS_PREFIX, serializedHeader);

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
            String serializedBoardState = mapper.writeValueAsString(game.getBoard());
            jedis.hset(SPECIFIED_GAME_PREFIX, "currentBoard", serializedBoardState);

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
            ArrayList<Integer> playerCaptures = getPlayerCaptures(jedis, gameId);
            playerCaptures.add(0);

            String serializedPlayerCaptures = mapper.writeValueAsString(playerCaptures);
            jedis.hset(SPECIFIED_GAME_PREFIX, "playerCaptures", serializedPlayerCaptures);

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
 


    public static void addToPenteGameLog(Jedis jedis, UUID gameId, PenteTurn turn) {

    }
}