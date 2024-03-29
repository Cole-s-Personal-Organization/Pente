package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import com.mycompany.app.Game.Pente.PenteGameModel;
import com.mycompany.app.WebServer.DBA.RedisPenteGameStore;
import com.mycompany.app.WebServer.Models.GameServerInfo;
import com.mycompany.app.WebServer.Models.GameServerInfo.GameRunState;
import com.mycompany.app.WebServer.WebServlets.Pente.PenteGameEndpointHandler;

import redis.clients.jedis.Jedis;

/**
 * This class of tests contains tests relating to making sure all RedisPenteGameStore functions are able to interact with redis on a cross 
 * service level.
 */
public class RedisPenteGameStoreIntegrationTests {

    // private static final int REDIS_PORT = 6379;
    private static final int REDIS_TEST_PORT = 4000;
    private static final String redisImageName = "redis:latest";

    private Jedis cache;

    @Rule
    public GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(redisImageName))
        .withCommand("--port " + REDIS_TEST_PORT)
        .withExposedPorts(REDIS_TEST_PORT);

    @Before
    public void setUp() {
        System.out.println("Connecting to redis instance at HOST: " + redis.getHost() + " PORT: " + redis.getMappedPort(REDIS_TEST_PORT));
        cache = new Jedis(redis.getHost(), redis.getMappedPort(REDIS_TEST_PORT));
    }

    @Test 
    public void testPingAgainstActiveConnection() {
        assertTrue("PONG".equalsIgnoreCase(cache.ping()));
    }

    /**
     * Basic create and list pente games
     */
    @Test
    public void testBasicCreateAndListPenteGamesOnEmptyCache() {
        Set<GameServerInfo> emptyPenteHeaders = RedisPenteGameStore.getPenteGameHeaders(cache);
        assertEquals(0, emptyPenteHeaders.size());

        // add a game
        UUID gameId = UUID.randomUUID();
        GameServerInfo game1header = new GameServerInfo(
            gameId, 
            "Game 1", 
            UUID.randomUUID(), 
            LocalDateTime.now().toString(), 
            GameRunState.Created);
        PenteGameModel game1Model = new PenteGameModel();
        RedisPenteGameStore.createPenteGame(cache, game1header, game1Model);

        // test for game existence
        Set<GameServerInfo> populatedPenteHeaders = RedisPenteGameStore.getPenteGameHeaders(cache);
        assertEquals(1, populatedPenteHeaders.size());

        cache.flushAll();
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
        PenteGameModel game1Model = new PenteGameModel();
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

        cache.flushAll();
    }

    /**
     * Basic test to ensure player capture counter is working
     */
    @Test 
    public void testBasicIncrementPlayerCaptureCounter() {
        UUID gameId = UUID.randomUUID();
        GameServerInfo game1header = new GameServerInfo(
            gameId, 
            "Game 1", 
            UUID.randomUUID(), 
            LocalDateTime.now().toString(), 
            GameRunState.Created);
        PenteGameModel game1Model = new PenteGameModel();
        RedisPenteGameStore.createPenteGame(cache, game1header, game1Model);
        
        // two new players 
        UUID player1Id = UUID.randomUUID();
        RedisPenteGameStore.addPlayerToGame(cache, gameId, player1Id);

        UUID player2Id = UUID.randomUUID();
        RedisPenteGameStore.addPlayerToGame(cache, gameId, player2Id);

        ArrayList<Integer> nonEmptyPlayerCaptures = RedisPenteGameStore.getPlayerCaptures(cache, gameId);
        assertEquals(2, nonEmptyPlayerCaptures.size());
        assertEquals(0, (int)nonEmptyPlayerCaptures.get(0));
        assertEquals(0, (int)nonEmptyPlayerCaptures.get(1));

        // add capture increment to new player
        RedisPenteGameStore.addPlayerCaptureByPlayerId(cache, gameId, player2Id);
        RedisPenteGameStore.addPlayerCaptureByPlayerId(cache, gameId, player1Id);
        RedisPenteGameStore.addPlayerCaptureByPlayerId(cache, gameId, player2Id);

        nonEmptyPlayerCaptures = RedisPenteGameStore.getPlayerCaptures(cache, gameId);
        assertEquals(2, nonEmptyPlayerCaptures.size());
        assertEquals(1, (int)nonEmptyPlayerCaptures.get(0));
        assertEquals(2, (int)nonEmptyPlayerCaptures.get(1));

        cache.flushAll();
    }

    @Test 
    public void getBoardState() {
        UUID gameId = UUID.randomUUID();
        GameServerInfo game1header = new GameServerInfo(
            gameId, 
            "Game 1", 
            UUID.randomUUID(), 
            LocalDateTime.now().toString(), 
            GameRunState.Created);
        PenteGameModel game1Model = new PenteGameModel();
        RedisPenteGameStore.createPenteGame(cache, game1header, game1Model);


    }

    @Test
    public void testStartGame() {

    } 
}
