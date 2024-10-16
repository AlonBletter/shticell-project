package server.servlets;

import com.google.gson.Gson;
import dto.SheetInfoDTO;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

@WebServlet(name = "Sheets List Servlet", urlPatterns = "/sheetsList")
public class SheetsListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Engine engine = ServletUtils.getEngine(getServletContext());
            List<SheetInfoDTO> sheetsList = engine.getSheetsInSystem();
            String json = gson.toJson(sheetsList);
            out.println(json);
            out.flush();
        }
    }
}