package server.servlets.sheet;

import dto.requestinfo.FilterParams;
import dto.sheet.SheetDTO;
import engine.Engine;
import engine.exception.InvalidCellBoundsException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.ServletUtils;
import server.utils.SessionUtils;

import java.io.IOException;

import static server.constants.Constants.GSON_INSTANCE;
import static server.utils.ServletUtils.handleInvalidCellBoundException;

@WebServlet(name = "Get Filtered Sheet Servlet", urlPatterns = "/sheet/filter")
public class GetFilteredSheetServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sheetName = SessionUtils.getSheetName(request);
        String username = SessionUtils.getUsername(request);
        if (username == null || sheetName == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());

        FilterParams filterParams = GSON_INSTANCE.fromJson(request.getReader(), FilterParams.class);

        response.setContentType("application/json");

        try {
            SheetDTO sheet = engine.getFilteredSheet(username, sheetName, filterParams.rangeToFilter(), filterParams.filterRequestValues());
            String jsonResponse = GSON_INSTANCE.toJson(sheet);
            response.getWriter().println(jsonResponse);
            response.getWriter().flush();
        } catch (InvalidCellBoundsException e) {
            handleInvalidCellBoundException(response, e);
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, e.getMessage());
        }
    }
}
