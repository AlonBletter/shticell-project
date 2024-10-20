package server.servlets.permission;

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

@WebServlet(name = "Request Permission Servlet", urlPatterns = "/requestPermission")
public class RequestPermissionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String sheetName = ServletUtils.validateRequiredParameter(request, "sheetName", response);
        String permission = ServletUtils.validateRequiredParameter(request, "permissionType", response);

        if (sheetName == null || permission == null) {
            return;
        }

        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        // TODO check if permission is none, maybe dont return the sheet dto

        response.setContentType("application/json");

        try {
            engine.requestPermission(sheetName, username, PermissionType.valueOf(permission.toUpperCase())); //TODO synchronized

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
