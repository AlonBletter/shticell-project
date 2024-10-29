package server.servlets.sheet;

import dto.info.UpdateInformation;
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

@WebServlet(name = "Update Cell Background Color Servlet", urlPatterns = "/sheet/cell/background/color")
public class UpdateCellBackgroundColorServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String username = SessionUtils.getUsername(req);
        String sheetName = SessionUtils.getSheetName(req);
        if (username == null || sheetName == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        UpdateInformation updateInformation = GSON_INSTANCE.fromJson(req.getReader(), UpdateInformation.class);

        try {
            engine.updateCellBackgroundColor(
                    username,
                    sheetName,
                    updateInformation.coordinate(),
                    updateInformation.value(),
                    updateInformation.version()
            );

            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(resp, e.getMessage());
        }
    }
}
