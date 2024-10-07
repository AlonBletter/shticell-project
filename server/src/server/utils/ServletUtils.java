package server.utils;

import engine.user.UserManager;
import engine.user.UserManagerImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import static server.constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";

	private static final Object userManagerLock = new Object();
	private static final Object chatManagerLock = new Object();

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
