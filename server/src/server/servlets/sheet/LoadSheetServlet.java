package server.servlets.sheet;

import engine.Engine;
import engine.exception.InvalidCellBoundsException;
import dto.coordinate.Coordinate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import server.utils.ServletUtils;
import server.utils.SessionUtils;

import java.io.IOException;


@WebServlet(name = "Load Sheet Servlet", urlPatterns = "/loadSheet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024 * 100)
public class LoadSheetServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");

        Engine engine = ServletUtils.getEngine(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Part filePart = request.getPart("file");

        try {
            engine.loadSheet(username, filePart.getInputStream());

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\"}");
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"File processing error\"}");
        } catch (InvalidCellBoundsException e) {
            handleInvalidCellBoundException(response, e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleInvalidCellBoundException(HttpServletResponse response, InvalidCellBoundsException e) throws IOException {
        Coordinate coordinate = e.getActualCoordinate();
        int sheetNumOfRows = e.getSheetNumOfRows();
        int sheetNumOfColumns = e.getSheetNumOfColumns();
        char sheetColumnRange = (char) (sheetNumOfColumns + 'A' - 1);
        char cellColumnChar = (char) (coordinate.column() + 'A' - 1);
        String message = e.getMessage() != null ? e.getMessage() : "";

        String errorMessage = message + " Expected column between A-" + sheetColumnRange +
                " and row between 1-" + sheetNumOfRows + ". " +
                "But received column [" + cellColumnChar + "] and row [" + coordinate.row() + "].";

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\":\"" + errorMessage + "\"}");
    }
}
