package com.mycompany.app.WebServer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnectionManager {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    private static JedisPool jedisPool;

    // Private constructor to prevent instantiation
    private RedisConnectionManager() {
    }

    // Lazy initialization with double-checked locking
    private static JedisPool getJedisPool() {
        if (jedisPool == null) {
            synchronized (RedisConnectionManager.class) {
                if (jedisPool == null) {
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    // Configure pool settings if needed

                    jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT);
                }
            }
        }
        return jedisPool;
    }

    // Get a Jedis instance from the pool
    public static Jedis getJedis() {
        return getJedisPool().getResource();
    }

    // Return Jedis instance to the pool
    public static void returnJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    // Destroy the Jedis pool when shutting down the application
    public static void destroyPool() {
        if (jedisPool != null) {
            jedisPool.destroy();
        }
    }
}
