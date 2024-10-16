package server.utils;

import engine.Engine;
import engine.EngineImpl;
import engine.user.UserManager;
import engine.user.UserManagerImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

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

//	public static ChatManager getChatManager(ServletContext servletContext) {
//		synchronized (chatManagerLock) {
//			if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
//				servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
//			}
//		}
//		return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
//	} //TODO when doing bonus

	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}
}
