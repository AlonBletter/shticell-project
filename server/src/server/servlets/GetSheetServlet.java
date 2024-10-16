package server.servlets;

import com.google.gson.Gson;
import dto.SheetDTO;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.ServletUtils;
import server.utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "Get Sheet Servlet", urlPatterns = "/getSheet")
public class GetSheetServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");

        String sheetName = request.getParameter("sheetName");

        Engine engine = ServletUtils.getEngine(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // TODO check if permission is none, maybe dont return the sheet dto

        try {
            SheetDTO sheet = engine.getSheet(sheetName);
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(sheet);
            response.getWriter().println(jsonResponse);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
