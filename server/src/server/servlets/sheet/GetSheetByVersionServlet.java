package server.servlets.sheet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.SheetDTO;
import engine.Engine;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.constants.Constants;
import server.utils.ServletUtils;
import server.utils.SessionUtils;

import java.io.BufferedReader;
import java.io.IOException;

import static server.constants.Constants.*;

@WebServlet(name = "Get Sheet By Version Servlet", urlPatterns = "/sheet/version/get")
public class GetSheetByVersionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        int version = ServletUtils.getIntParameter(request, VERSION, response);

        if(version == INT_PARAMETER_ERROR) {
            return;
        }

        String sheetName = SessionUtils.getSheetName(request);
        String username = SessionUtils.getUsername(request);
        if (username == null || sheetName == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());

        response.setContentType("application/json");

        try {
            SheetDTO sheet = engine.getSheetByVersion(username, sheetName, version); //TODO synchronized
            String jsonResponse = GSON_INSTANCE.toJson(sheet);
            response.getWriter().println(jsonResponse);
            response.getWriter().flush();
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, e.getMessage());
        }
    }
}