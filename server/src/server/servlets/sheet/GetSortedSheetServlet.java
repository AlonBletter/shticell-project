package server.servlets.sheet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.LinkedList;
import java.util.List;

import static server.constants.Constants.GSON_INSTANCE;

@WebServlet(name = "Get Sorted Sheet Servlet", urlPatterns = "/sheet/sort")
public class GetSortedSheetServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String sheetName = SessionUtils.getSheetName(request);
        String username = SessionUtils.getUsername(request);
        if (username == null || sheetName == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());

        JsonObject jsonObject = JsonParser.parseReader(request.getReader()).getAsJsonObject();
        String range = jsonObject.get("range").getAsString();
        JsonArray columnsArray = jsonObject.getAsJsonArray("columns");

        List<String> columns = new LinkedList<>();
        for (JsonElement columnElement : columnsArray) {
            columns.add(columnElement.getAsString());
        }

        response.setContentType("application/json");

        try {
            SheetDTO sheet = engine.getSortedSheet(username, sheetName, range, columns); //TODO synchronized
            String jsonResponse = GSON_INSTANCE.toJson(sheet);
            response.getWriter().println(jsonResponse);
            response.getWriter().flush();
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, e.getMessage());
        }
    }
}
