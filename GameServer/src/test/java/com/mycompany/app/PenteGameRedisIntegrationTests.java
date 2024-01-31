package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import com.mycompany.app.Game.Pente.PenteGameBoardModel;
import com.mycompany.app.Game.Pente.PenteGameModel;
import com.mycompany.app.Game.Pente.PenteGameSettings;
import com.mycompany.app.WebServer.ClientProxy;
import com.mycompany.app.WebServer.RedisBackedCache;
import com.mycompany.app.WebServer.DBO.RedisPenteGameStore;
import com.mycompany.app.WebServer.Endpoints.PenteGameEndpointHandler;
import com.mycompany.app.WebServer.Models.GameServerInfo;
import com.mycompany.app.WebServer.Models.GameServerInfo.GameRunState;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

/**
 * This class of tests is 
 */
public class PenteGameRedisIntegrationTests {

    private static final int REDIS_PORT = 6379;
    private static final String redisImageName = "redis:latest";

    private Jedis cache;

    @Rule
    public GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(redisImageName))
        .withExposedPorts(REDIS_PORT);

    @Before
    public void setUp() {
        cache = new Jedis(redis.getHost(), redis.getMappedPort(REDIS_PORT));
    }

    /**
     * Simple put and get example to demonstrate a connection with a redis instance can be established.
     */
    @Test
    public void testFindingAnInsertedValue() {
        cache.set("foo", "FOO");
        String foundObject = cache.get("foo");

        assertNotNull(foundObject);
        assertEquals("FOO", foundObject);
    }

    /**
     * Another simple get example on a redis instance with a non-inserted key to demonstrate connection establishment. 
     */
    @Test
    public void testNotFindingAValueThatWasNotInserted() {
        String foundObject = cache.get("bar");

        assertNull(foundObject);
    }

    @Test
    public void testServeletConstruction() {
        PenteGameEndpointHandler handler = new PenteGameEndpointHandler(cache);

        assertNotNull(handler);
    }

    @Test
    public void testBasicCreateAndListPenteGamesOnEmptyCache() {
        Set<GameServerInfo> emptyPenteHeaders = RedisPenteGameStore.getPenteGameHeaders(cache);
        assertEquals(0, emptyPenteHeaders.size());

        // add a game
        GameServerInfo game1header = new GameServerInfo(
            UUID.randomUUID(), 
            "Game 1", 
            UUID.randomUUID(), 
            LocalDateTime.now().toString(), 
            GameRunState.Created);
        PenteGameBoardModel game1Board = new PenteGameBoardModel();
        PenteGameSettings game1Settings = new PenteGameSettings.PenteGameSettingsBuilder().build();
        PenteGameModel game1Model = new PenteGameModel(game1Settings, game1Board);
        RedisPenteGameStore.createPenteGame(cache, game1header, game1Model);

        // test for game existence
        Set<GameServerInfo> populatedPenteHeaders = RedisPenteGameStore.getPenteGameHeaders(cache);
        assertEquals(1, populatedPenteHeaders.size());
    }

    /**
     * The creator of the game, when they leave, should check 
     */
    @Test 
    public void testCreatorLeavesGame() {

    } 

    /**
     * Basic test to ensure add and remove player functionality works
     */
    @Test 
    public void testBasicAddAndRemovePlayerToRunningGame() {
        UUID gameId = UUID.randomUUID();
        GameServerInfo game1header = new GameServerInfo(
            gameId, 
            "Game 1", 
            UUID.randomUUID(), 
            LocalDateTime.now().toString(), 
            GameRunState.Created);
        PenteGameBoardModel game1Board = new PenteGameBoardModel();
        PenteGameSettings game1Settings = new PenteGameSettings.PenteGameSettingsBuilder().build();
        PenteGameModel game1Model = new PenteGameModel(game1Settings, game1Board);
        RedisPenteGameStore.createPenteGame(cache, game1header, game1Model);

        // game player list should be empty
        ArrayList<UUID> emptyPlayerIds = RedisPenteGameStore.getPlayersInGame(cache, gameId);
        assertNotNull(emptyPlayerIds);
        assertEquals(0, emptyPlayerIds.size());

        // new player 
        UUID playerId = UUID.randomUUID();

        RedisPenteGameStore.addPlayerToGame(cache, gameId, playerId);

        // game player list should have the new player
        ArrayList<UUID> nonEmptyPlayerId = RedisPenteGameStore.getPlayersInGame(cache, gameId);
        assertNotNull(nonEmptyPlayerId);
        assertEquals(1, nonEmptyPlayerId.size());
        assertEquals(playerId, nonEmptyPlayerId.get(0));

        // remove new player 
        RedisPenteGameStore.removePlayerFromGame(cache, gameId, playerId);

        // game player list should be empty again
        ArrayList<UUID> reEmptiedPlayerIds = RedisPenteGameStore.getPlayersInGame(cache, gameId);
        assertNotNull(reEmptiedPlayerIds);
        assertEquals(0, reEmptiedPlayerIds.size());
    }

    @Test 
    public void testBasicIncrementPlayerCaptureCounter() {
        UUID gameId = UUID.randomUUID();
        GameServerInfo game1header = new GameServerInfo(
            gameId, 
            "Game 1", 
            UUID.randomUUID(), 
            LocalDateTime.now().toString(), 
            GameRunState.Created);
        PenteGameBoardModel game1Board = new PenteGameBoardModel();
        PenteGameSettings game1Settings = new PenteGameSettings.PenteGameSettingsBuilder().build();
        PenteGameModel game1Model = new PenteGameModel(game1Settings, game1Board);
        RedisPenteGameStore.createPenteGame(cache, game1header, game1Model);


        // player capture list should be initialized to empty
        ArrayList<Integer> emptyPlayerCaptures = RedisPenteGameStore.getPlayerCaptures(cache, gameId);
        System.out.println("EMPT PLAYER CAPS " + emptyPlayerCaptures );
        assertEquals(0, emptyPlayerCaptures.size());

        // new player 
        UUID playerId = UUID.randomUUID();
        RedisPenteGameStore.addPlayerToGame(cache, gameId, playerId);

        ArrayList<Integer> nonEmptyPlayerCaptures = RedisPenteGameStore.getPlayerCaptures(cache, gameId);
        assertEquals(1, nonEmptyPlayerCaptures.size());
        int numCaptures = nonEmptyPlayerCaptures.get(0);
        assertEquals(0, numCaptures);

        RedisPenteGameStore.addPlayerCaptureByPlayerId(cache, gameId, playerId);
        nonEmptyPlayerCaptures = RedisPenteGameStore.getPlayerCaptures(cache, gameId);
        assertEquals(1, nonEmptyPlayerCaptures.size());
        numCaptures = nonEmptyPlayerCaptures.get(0);
        assertEquals(0, numCaptures);
    }

    @Test
    public void testStartGame() {

    } 
}
