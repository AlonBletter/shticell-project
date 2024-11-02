package server.utils;

import com.google.gson.JsonObject;
import dto.coordinate.Coordinate;
import engine.Engine;
import engine.EngineImpl;
import engine.chat.ChatManager;
import engine.exception.InvalidCellBoundsException;
import engine.user.UserManager;
import engine.user.UserManagerImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static server.constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
	private static final String ENGINE_ATTRIBUTE_NAME = "engine";

	private static final Object userManagerLock = new Object();
	private static final Object chatManagerLock = new Object();
	private static final Object engineLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {
		UserManager userManager = (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);

		if (userManager == null) {
			synchronized (userManagerLock) {
				userManager = (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
				if (userManager == null) {
					userManager = new UserManagerImpl();
					servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, userManager);
				}
			}
		}
		return userManager;
	}

	public static Engine getEngine(ServletContext servletContext) {
		Engine engine = (Engine) servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME);

		if (engine == null) {
			synchronized (engineLock) {
				engine = (Engine) servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME);
				if (engine == null) {
					engine = new EngineImpl();
					servletContext.setAttribute(ENGINE_ATTRIBUTE_NAME, engine);
				}
			}
		}

		return engine;
	}

	public static ChatManager getChatManager(ServletContext servletContext) {
		synchronized (chatManagerLock) {
			if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
			}
		}
		return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
	}

	public static int getIntParameter(HttpServletRequest request, String name, HttpServletResponse response) throws IOException {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		sendErrorResponse(response, "Missing or invalid 'version' parameter. An integer is required.");
		return INT_PARAMETER_ERROR;
	}

	public static String validateRequiredParameter(HttpServletRequest request, String paramName, HttpServletResponse response) throws IOException {
		String paramValue = request.getParameter(paramName);

		if (paramValue == null || paramValue.trim().isEmpty()) {
			sendErrorResponse(response, "Missing required parameter: " + paramName);
			return null;
		}
		return paramValue;
	}

	public static void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

		JsonObject errorResponse = new JsonObject();
		errorResponse.addProperty("error", errorMessage);

		response.getWriter().write(errorResponse.toString());
	}


	public static void handleInvalidCellBoundException(HttpServletResponse response, InvalidCellBoundsException e) throws IOException {
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
