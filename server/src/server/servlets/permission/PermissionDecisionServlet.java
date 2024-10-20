package server.servlets.permission;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.permission.PermissionType;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.ServletUtils;
import server.utils.SessionUtils;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "Permission Decision Servlet", urlPatterns = "/permission/request/decision")
public class PermissionDecisionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        BufferedReader reader = request.getReader();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

        int requestId = jsonObject.get("requestId").getAsInt();
        String sheetName = jsonObject.get("sheetName").getAsString();
        boolean decision = jsonObject.get("decision").getAsBoolean();

        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        // TODO check if permission is none, maybe dont return the sheet dto

        response.setContentType("application/json");

        try {
            engine.handlePermissionRequest(requestId, sheetName, username, decision); //TODO synchronized
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}