package server.servlets.permission;

import com.google.gson.Gson;
import dto.permission.PermissionInfoDTO;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "Get Permission Requests Servlet", urlPatterns = "/getPermissionRequests")
public class GetPermissionRequestsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String sheetName = ServletUtils.validateRequiredParameter(request, "sheetName", response);

        if (sheetName == null) {
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            response.setContentType("application/json");
            Engine engine = ServletUtils.getEngine(getServletContext());
            List<PermissionInfoDTO> permissionRequests = engine.getSheetPermissionRequests(sheetName);
            String json = gson.toJson(permissionRequests);
            out.println(json);
            out.flush();
        }
    }
}