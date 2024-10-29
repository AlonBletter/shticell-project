package server.servlets.sheet;

import com.google.gson.JsonObject;
import dto.range.RangeDTO;
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

@WebServlet(name = "Add Range Servlet", urlPatterns = "/sheet/range/add")
public class AddRangeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String username = SessionUtils.getUsername(req);
        String sheetName = SessionUtils.getSheetName(req);
        if (username == null || sheetName == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        JsonObject jsonObject = GSON_INSTANCE.fromJson(req.getReader(), JsonObject.class);

        String rangeName = jsonObject.get("rangeName").getAsString();
        String coordinates = jsonObject.get("coordinates").getAsString();
        int version = jsonObject.get("version").getAsInt();

        try {
            RangeDTO rangeDTO = engine.addRange(username, sheetName, rangeName, coordinates, version);
            String jsonResponse = GSON_INSTANCE.toJson(rangeDTO);
            resp.getWriter().write(jsonResponse);
            resp.getWriter().flush();
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(resp, e.getMessage());
        }
    }
}