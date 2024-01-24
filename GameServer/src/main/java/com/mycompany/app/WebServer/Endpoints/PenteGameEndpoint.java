package com.mycompany.app.WebServer.Endpoints;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import com.mycompany.app.WebServer.UuidValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/pente-game/*")
public class PenteGameEndpoint extends HttpServlet {

    private void handleGetEndpoint(List<String> currEndpointList) {
        // return for invalid empty list call
        if (currEndpointList.size() == 0) {
            return;
        }

        String currEndpointPathElement = currEndpointList.remove(0);

        switch (currEndpointPathElement) {
            case "/list":
                // Handle listing available games
                // Example: /game/list
                break;

            case "/create":
                // Handle creating a new game
                // Example: /game/create
                break;
        
            default:
                // Handle possible uuid specifier
                // otherwise invalid request recieved
                if (UuidValidator.isValidUUID(currEndpointPathElement)) {
                    UUID gameId = UUID.fromString(currEndpointPathElement);

                    handleGameSpecificGetEndpoint(
                        gameId, 
                        currEndpointList
                    );
                }

                break;
        }
    }

    private void handleGameSpecificGetEndpoint(UUID gameId, List<String> currEndpointList) {

        // Handle other cases, such as getting game state
        // Example: /game/{gameId}

        if (currEndpointList.size() == 0) {
            // Handle getting specific game information
            // Example: GET /game/{game_id}
            return;
        }


        if (rootUriPath.startsWith("/events")) {
            // Handle WebSocket events
            // Example: /game/{gameId}/events
        } 
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // String[] pathInfoList = req.getPathInfo().split("/");
        List<String> pathInfoList = Arrays.asList(req.getPathInfo().split("/"));
        

        // there will be no /pente-game/ endpoint
        if (pathInfoList.size() == 0) {
            return;
        }

        handleGetEndpoint(pathInfoList);


        // Set content type to text/event-stream
        resp.setContentType("text/event-stream");
        resp.setCharacterEncoding("UTF-8");

        // Enable caching control to prevent caching of the SSE resp
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");

        try (PrintWriter out = resp.getWriter()) {
            // Send a simple SSE event
            out.write("data: Hello, SSE!\n\n");
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo.startsWith("/join")) {
            // Handle joining a game
            // Example: /game/{gameId}/join
        } else if (pathInfo.startsWith("/move")) {
            // Handle making a move
            // Example: /game/{gameId}/move
        } else if (pathInfo.startsWith("/leave")) {
            // Handle leaving a game
            // Example: /game/{gameId}/leave
        }
    }    
}
