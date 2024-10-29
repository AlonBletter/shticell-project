package server.servlets.sheet;

import dto.requestinfo.UpdateInformation;
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
import java.io.PrintWriter;

import static server.constants.Constants.GSON_INSTANCE;

@WebServlet(name = "Get Expected Value Servlet", urlPatterns = "/sheet/whatIf")
public class GetExpectedValueServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String username = SessionUtils.getUsername(req);
        String sheetName = SessionUtils.getSheetName(req);
        if (username == null || sheetName == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        UpdateInformation updateInformation = GSON_INSTANCE.fromJson(req.getReader(), UpdateInformation.class);

        try {
            SheetDTO sheetDTO = engine.getExpectedValue(
                    username,
                    sheetName,
                    updateInformation.coordinate(),
                    updateInformation.value(),
                    updateInformation.version()
            );

            String jsonSheet = GSON_INSTANCE.toJson(sheetDTO);
            out.println(jsonSheet);
            out.flush();
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(resp, e.getMessage());
        }
    }
}
