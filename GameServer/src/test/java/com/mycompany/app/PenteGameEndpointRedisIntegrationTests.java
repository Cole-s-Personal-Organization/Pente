package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import com.mycompany.app.WebServer.RedisBackedCache;
import com.mycompany.app.WebServer.Endpoints.PenteGameEndpointHandler;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

/**
 * This class of tests is 
 */
public class PenteGameEndpointRedisIntegrationTests {

    private static final int REDIS_PORT = 6379;
    private static final String redisImageName = "redis:latest";

    private RedisBackedCache cache;

    @Rule
    public GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(redisImageName))
        .withExposedPorts(REDIS_PORT);

    @Before
    public void setUp() {
        Jedis jedis = new Jedis(redis.getHost(), redis.getMappedPort(REDIS_PORT));

        cache = new RedisBackedCache(jedis, "test");
    }

    /**
     * Simple put and get example to demonstrate a connection with a redis instance can be established.
     */
    @Test
    public void testFindingAnInsertedValue() {
        cache.put("foo", "FOO");
        Optional<String> foundObject = cache.get("foo", String.class);

        assertTrue(foundObject.isPresent());
        assertEquals("FOO", foundObject.get());
    }

    /**
     * Another simple get example on a redis instance with a non-inserted key to demonstrate connection establishment. 
     */
    @Test
    public void testNotFindingAValueThatWasNotInserted() {
        Optional<String> foundObject = cache.get("bar", String.class);

        assertFalse(foundObject.isPresent());
    }

    @Test
    public void testServeletConstruction() {
        PenteGameEndpointHandler handler = new PenteGameEndpointHandler(cache);

        assertNotNull(handler);
    }

    @Test
    public void testBasicListAllPenteGamesOnEmptyCache() {

    }
}
