package server.servlets.sheet;

import com.google.gson.JsonObject;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.ServletUtils;
import server.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "Get Latest Version Number Servlet", urlPatterns = "/sheet/version/last/number")
public class GetLatestVersionNumberServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String sheetName = SessionUtils.getSheetName(request);
        if (sheetName == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            int sheetLatestVersion = engine.getLatestVersion(username, sheetName);
            JsonObject json = new JsonObject();
            json.addProperty("version", sheetLatestVersion);
            String jsonString = json.toString();
            out.println(jsonString);
            out.flush();
        }
    }
}