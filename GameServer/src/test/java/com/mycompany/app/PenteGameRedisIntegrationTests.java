package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import com.mycompany.app.WebServer.DBO.RedisPenteGameStore;
import com.mycompany.app.WebServer.Endpoints.PenteGameEndpointHandler;
import com.mycompany.app.WebServer.Models.GameServerInfo;
import com.mycompany.app.WebServer.Models.GameServerInfo.GameRunState;

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
