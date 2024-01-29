package com.mycompany.app.WebServer;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;

public class RedisBackedCache {
    private final Jedis jedis;

    private final String cacheName;

    private final ObjectMapper mapper;

    public RedisBackedCache(Jedis jedis, String cacheName) {
        this.jedis = jedis;
        this.cacheName = cacheName;
        this.mapper = new ObjectMapper();
    }

    public void put(String key, Object value) {
        String jsonValue;
        try {
            jsonValue = mapper.writeValueAsString(value);
            this.jedis.hset(this.cacheName, key, jsonValue);
        } catch (JsonProcessingException e) {
            System.out.println("Improperly formatted json passed to redis cache.");
            return;
        }
    }

    public <T> Optional<T> get(String key, Class<T> expectedClass) {
        String foundJson = this.jedis.hget(this.cacheName, key);

        if (foundJson == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(mapper.readValue(foundJson, expectedClass));
        } catch (Exception e) {
            System.out.println("Invalid read occured.");
        }
        return null;
    }

    public void hset(String hashName, String field, String value) {
        this.jedis.hset(hashName, field, value);
    }
}
