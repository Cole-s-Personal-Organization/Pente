package com.mycompany.app.WebServer;

import java.time.Duration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnectionManager {
    private static final String REDIS_HOST = "redis";
    private static final int REDIS_PORT = 6379;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 1000; // 1 second delay between retries

    private String dbURL;
	private String user;
	private String password;

    private JedisPool jedisPool;

    // Private constructor to prevent instantiation
    public RedisConnectionManager(String url, String user, String password) {
        this.dbURL = url;
        this.user = user;
        this.password = password;

        JedisPoolConfig poolConfig = buildPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, "redis");
    }

    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    // Destroy the Jedis pool when shutting down the application
    public void destroyPool() {
        if (jedisPool != null) {
            jedisPool.destroy();
        }
    }
}
