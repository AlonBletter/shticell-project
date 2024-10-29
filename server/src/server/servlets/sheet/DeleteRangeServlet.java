package server.servlets.sheet;

import com.google.gson.JsonObject;
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

@WebServlet(name = "Delete Range Servlet", urlPatterns = "/sheet/range/delete")
public class DeleteRangeServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        int version = jsonObject.get("version").getAsInt();

        try {
            engine.deleteRange(username, sheetName, rangeName, version);
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(resp, e.getMessage());
        }
    }
}
