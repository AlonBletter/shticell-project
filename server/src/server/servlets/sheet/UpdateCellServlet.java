package server.servlets.sheet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.CoordinateAndValue;
import dto.SheetDTO;
import engine.Engine;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.ServletUtils;
import server.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static server.constants.Constants.GSON_INSTANCE;
import static server.constants.Constants.SHEET_NAME;

@WebServlet(name = "Update Cell Servlet", urlPatterns = "/sheet/update")
public class UpdateCellServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String username = SessionUtils.getUsername(req);
        String sheetName = SessionUtils.getSheetName(req);
        if (username == null || sheetName == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        CoordinateAndValue cav = GSON_INSTANCE.fromJson(req.getReader(), CoordinateAndValue.class);

        try {
            SheetDTO sheetDTO = engine.updateCell(username, sheetName, cav.coordinate(), cav.value());
            if (sheetDTO != null) {
                String jsonSheet = GSON_INSTANCE.toJson(sheetDTO);
                out.println(jsonSheet);
                out.flush();
            } else {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(resp, e.getMessage());
        }
    }
}
