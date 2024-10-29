package server.servlets.sheet;

import dto.sheet.SheetDTO;
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
import static server.constants.Constants.SHEET_NAME;

@WebServlet(name = "Get Sheet Servlet", urlPatterns = "/getSheet")
public class GetSheetServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String sheetName = ServletUtils.validateRequiredParameter(request, SHEET_NAME, response);
        if (sheetName == null) {
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        response.setContentType("application/json");

        try {
            SheetDTO sheet = engine.getSheet(username, sheetName); //TODO synchronized
            request.getSession(true).setAttribute(SHEET_NAME, sheetName);
            String jsonResponse = GSON_INSTANCE.toJson(sheet);
            response.getWriter().println(jsonResponse);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
