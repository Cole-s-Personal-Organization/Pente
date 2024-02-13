package com.mycompany.app.WebServer.Models;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PenteGameEvent {

    public enum PenteGameEventType {
        TurnTaken,
        GameStart,
        GameEnd,
        PlayerJoin,
        PlayerDisconnect
    }

    private UUID eventId;
    private PenteGameEventType event;
    private JsonNode data;


    public PenteGameEvent(UUID eventId, PenteGameEventType event, JsonNode data) {
        this.eventId = eventId;
        this.event = event;
        this.data = data;
    }
    

    public UUID getEventId() {
        return eventId;
    }

    public PenteGameEventType getEvent() {
        return event;
    }

    public JsonNode getData() {
        return data;
    }

    public String toJsonString() {
        String serializedEvent = ""; 
        try {
            serializedEvent = new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return serializedEvent;
    }
}
