package com.mycompany.app.WebServer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/hello")
public class HelloWorldServlet extends HttpServlet {

    ObjectMapper mapper = new ObjectMapper();

    private class HelloWorldObject {
        public String helloAttr = "Hello from Object";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HelloWorldObject helloWorldObject = new HelloWorldObject();

        String helloWorldJsonStr = this.mapper.writeValueAsString(helloWorldObject);
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(helloWorldJsonStr);
        out.flush();   
    }

    // @Override
    // protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //     response.setContentType("text/html");
    //     PrintWriter out = response.getWriter();
    //     out.println("<html><body>");
    //     out.println("<h1>Hello, World!</h1>");
    //     out.println("</body></html>");
    // }

    
}
