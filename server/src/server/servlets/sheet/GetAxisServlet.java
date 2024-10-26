package server.servlets.sheet;

import engine.Engine;
import engine.sheet.coordinate.Coordinate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.ServletUtils;
import server.utils.SessionUtils;

import java.io.IOException;
import java.util.List;

import static server.constants.Constants.GSON_INSTANCE;

@WebServlet(name = "Get Axis Servlet", urlPatterns = "/sheet/axis")
public class GetAxisServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String username = SessionUtils.getUsername(req);
        String sheetName = SessionUtils.getSheetName(req);
        if (username == null || sheetName == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        String axisRange = ServletUtils.validateRequiredParameter(req, "axisRange", resp);

        try {
            List<Coordinate> coordinateList = engine.getAxis(username, sheetName, axisRange);
            String jsonResponse = GSON_INSTANCE.toJson(coordinateList);
            resp.getWriter().write(jsonResponse);
            resp.getWriter().flush();
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(resp, e.getMessage());
        }
    }
}