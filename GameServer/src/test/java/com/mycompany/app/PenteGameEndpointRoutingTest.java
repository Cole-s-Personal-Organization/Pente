package com.mycompany.app;

import org.junit.Test;

import com.mycompany.app.WebServer.WebServlets.Pente.PenteGameEndpointHandler;
import com.mycompany.app.WebServer.WebServlets.Pente.PenteGameEndpointHandler.EndpointRouterResponseId;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class PenteGameEndpointRoutingTest {
    @Test
    public void testRouteGetEndpoints_validUUID_head() {
        ArrayList<String> pathInfo = new ArrayList<>();
        String validUuid = UUID.randomUUID().toString();
        pathInfo.add(validUuid);
        pathInfo.add("head");

        assertEquals(EndpointRouterResponseId.getSpecificGameHeaderByGameId, PenteGameEndpointHandler.routeGetEndpoints(pathInfo));
    }

    @Test
    public void testRouteGetEndpoints_validUUID_board() {
        ArrayList<String> pathInfo = new ArrayList<>();
        String validUuid = UUID.randomUUID().toString();
        pathInfo.add(validUuid);
        pathInfo.add("board");

        assertEquals(EndpointRouterResponseId.getSpecificGameBoardByGameId, PenteGameEndpointHandler.routeGetEndpoints(pathInfo));
    }

    @Test
    public void testRouteGetEndpoints_list_head() {
        ArrayList<String> pathInfo = new ArrayList<>();
        pathInfo.add("list");
        pathInfo.add("head");

        assertEquals(EndpointRouterResponseId.getlistGameHeaders, PenteGameEndpointHandler.routeGetEndpoints(pathInfo));
    }

    @Test
    public void testRouteGetEndpoints_invalidUUID() {
        ArrayList<String> pathInfo = new ArrayList<>();
        pathInfo.add("invalid-uuid");

        assertEquals(EndpointRouterResponseId.missingEndpointError, PenteGameEndpointHandler.routeGetEndpoints(pathInfo));
    }

    @Test
    public void testRouteGetEndpoints_invalidList() {
        ArrayList<String> pathInfo = new ArrayList<>();
        pathInfo.add("list");
        pathInfo.add("invalid");

        assertEquals(EndpointRouterResponseId.missingEndpointError, PenteGameEndpointHandler.routeGetEndpoints(pathInfo));
    }

    @Test
    public void testRouteGetEndpoints_emptyPath() {
        ArrayList<String> pathInfo = new ArrayList<>();

        assertEquals(EndpointRouterResponseId.missingEndpointError, PenteGameEndpointHandler.routeGetEndpoints(pathInfo));
    }

    @Test
    public void testRoutePostEndpoints_createGame() {
        ArrayList<String> pathInfo = new ArrayList<>();
        pathInfo.add("create");

        assertEquals(EndpointRouterResponseId.postCreateGame, PenteGameEndpointHandler.routePostEndpoints(pathInfo));
    }

    @Test
    public void testRoutePostEndpoints_setSettingsGame() {
        ArrayList<String> pathInfo = new ArrayList<>();
        String validUuid = UUID.randomUUID().toString();
        pathInfo.add(validUuid);
        pathInfo.add("settings");

        assertEquals(EndpointRouterResponseId.postGameSettingsByGameId, PenteGameEndpointHandler.routePostEndpoints(pathInfo));
    }

    @Test
    public void testRoutePostEndpoints_setStartGame() {
        ArrayList<String> pathInfo = new ArrayList<>();
        String validUuid = UUID.randomUUID().toString();
        pathInfo.add(validUuid);
        pathInfo.add("start");

        assertEquals(EndpointRouterResponseId.postGameStartByGameId, PenteGameEndpointHandler.routePostEndpoints(pathInfo));
    }

    @Test
    public void testRoutePostEndpoints_setMoveGame() {
        ArrayList<String> pathInfo = new ArrayList<>();
        String validUuid = UUID.randomUUID().toString();
        pathInfo.add(validUuid);
        pathInfo.add("move");

        assertEquals(EndpointRouterResponseId.postGameMoveByGameId, PenteGameEndpointHandler.routePostEndpoints(pathInfo));
    }

    @Test
    public void testRoutePostEndpoints_setLeaveGame() {
        ArrayList<String> pathInfo = new ArrayList<>();
        String validUuid = UUID.randomUUID().toString();
        pathInfo.add(validUuid);
        pathInfo.add("leave");

        assertEquals(EndpointRouterResponseId.postLeaveGameByGameId, PenteGameEndpointHandler.routePostEndpoints(pathInfo));
    }

    @Test
    public void testRoutePostEndpoints_setJoinGame() {
        ArrayList<String> pathInfo = new ArrayList<>();
        String validUuid = UUID.randomUUID().toString();
        pathInfo.add(validUuid);
        pathInfo.add("join");

        assertEquals(EndpointRouterResponseId.postJoinGameByGameId, PenteGameEndpointHandler.routePostEndpoints(pathInfo));
    }

    @Test
    public void testRoutePostEndpoints_invalidUUID() {
        ArrayList<String> pathInfo = new ArrayList<>();
        pathInfo.add("invalid-uuid");

        assertEquals(EndpointRouterResponseId.missingEndpointError, PenteGameEndpointHandler.routeGetEndpoints(pathInfo));
    }

    @Test
    public void testRoutePostEndpoints_invalidList() {
        ArrayList<String> pathInfo = new ArrayList<>();
        pathInfo.add("list");
        pathInfo.add("invalid");

        assertEquals(EndpointRouterResponseId.missingEndpointError, PenteGameEndpointHandler.routeGetEndpoints(pathInfo));
    }
}
