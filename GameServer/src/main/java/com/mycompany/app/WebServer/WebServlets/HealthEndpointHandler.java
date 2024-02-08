package com.mycompany.app.WebServer.WebServlets;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * An endpoint used to check if the gameserver service is running and handling requests from a requestee.
 * @author Cole
 */
@WebServlet("/health")
public class HealthEndpointHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Service is healthy");
    }
}
