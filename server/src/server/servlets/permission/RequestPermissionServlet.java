package server.servlets.permission;

import com.google.gson.JsonObject;
import dto.permission.PermissionType;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.ServletUtils;
import server.utils.SessionUtils;

import java.io.IOException;

import static server.constants.Constants.GSON_INSTANCE;

@WebServlet(name = "Request Permission Servlet", urlPatterns = "/requestPermission")
public class RequestPermissionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObject jsonObject = GSON_INSTANCE.fromJson(request.getReader(), JsonObject.class);

        String sheetName = jsonObject.get("sheetName").getAsString();
        String permission = jsonObject.get("permissionType").getAsString();

        if (sheetName == null || permission == null) {
            return;
        }

        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());

        response.setContentType("application/json");

        try {
            engine.requestPermission(sheetName, username, PermissionType.valueOf(permission.toUpperCase()));

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
