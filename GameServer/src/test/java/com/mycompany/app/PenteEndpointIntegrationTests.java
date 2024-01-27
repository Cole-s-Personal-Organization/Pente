package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public class PenteEndpointIntegrationTests {

    private static final int REDIS_PORT = 6379;
    private static final int GAMESERVER_PORT = 8080;
    private static final String pathToComposeFile = System.getProperty("user.dir") + "/../docker-compose.yml";
    private static final String redisImageName = "redis:latest";

    private RedisBackedCache cache;

    // @Rule
    // public GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(redisImageName))
    //     .withExposedPorts(REDIS_PORT)
    //     .waitingFor(Wait.forListeningPort());

    @ClassRule
    public static ComposeContainer environment = new ComposeContainer(new File(pathToComposeFile))
        .withExposedService("redis_1", REDIS_PORT, Wait.forListeningPort())
        .withExposedService("gameserver_1", GAMESERVER_PORT, Wait.forListeningPort());

    @Before
    public void setup() {
        Jedis jedis = new Jedis(new HostAndPort("localhost", REDIS_PORT));
        cache = new RedisBackedCache(jedis, "test");
    }

    @Test
    public void testContainersHealthCheck() {

        // fail if docker containers not started
        if (cache == null) {
            System.err.println("Redis container not initialized correctly.");
            fail();
        }
        // String gameserverIpAddress = dockerComposeContainer.getServiceHost("gameserver-backend", GAMESERVER_PORT);
        // int gameserverPort = dockerComposeContainer.getServicePort("gameserver-backend", GAMESERVER_PORT);

        // // Create URL for the health endpoint
        // try {
        //     URL gameserverHealthCheckUrl = new URL("http://" + gameserverIpAddress + ":" + gameserverPort + "/health");

        //     // Perform a simple HTTP GET request to the health endpoint
        //     HttpURLConnection connection = (HttpURLConnection) gameserverHealthCheckUrl.openConnection();
        //     connection.setRequestMethod("GET");

        //     // Check if the response code is 200 (OK)
        //     int responseCode = connection.getResponseCode();
        //     assertEquals(200, responseCode);
        // } catch (Exception e) {
        //     // TODO: handle exception
        // }


        // Redis Health Check
        // try {
        //     String pingResponse = testRedisInst.ping();
        //     assertEquals("PONG", pingResponse);
        // } finally {
        //     testRedisInst.close();
        // }
    }

    @Test
    public void testFindingAnInsertedValue() {
        cache.put("foo", "FOO");
        Optional<String> foundObject = cache.get("foo", String.class);

        assertTrue(foundObject.isPresent());
        assertEquals("FOO", foundObject.get());
    }

    @Test
    public void testNotFindingAValueThatWasNotInserted() {
        Optional<String> foundObject = cache.get("bar", String.class);

        assertFalse(foundObject.isPresent());
    }
    
    // @Test
    // public void getAllGames() {
    //     try {
    //         // Specify the URL of your servlet endpoint
    //         URL url = new URL("http://localhost:8080/your-servlet-endpoint");

    //         // Open a connection to the URL
    //         HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    //         // Set the request method to GET
    //         connection.setRequestMethod("GET");

    //         // Get the response code
    //         int responseCode = connection.getResponseCode();
    //         System.out.println("Response Code: " + responseCode);

    //         // Read the response content
    //         try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
    //             String line;
    //             StringBuilder responseContent = new StringBuilder();

    //             while ((line = reader.readLine()) != null) {
    //                 responseContent.append(line);
    //             }

    //             // Print the response content
    //             System.out.println("Response Content: " + responseContent.toString());
    //         }

    //         // Close the connection
    //         connection.disconnect();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
        
    // }
}
