package client.util;

import com.google.gson.Gson;

public class Constants {

    public final static int REFRESH_RATE = 2000;

    // FXML resources locations
    public final static String MAIN_SCREEN_FXML_RESOURCE_LOCATION = "/client/component/main/app.fxml";
    public final static String LOGIN_SCREEN_FXML_RESOURCE_LOCATION = "/client/component/login/login.fxml";
    public final static String DASHBOARD_FXML_RESOURCE_LOCATION = "/client/component/dashboard/dashboard.fxml";
    public final static String SHEET_FXML_RESOURCE_LOCATION = "/client/component/sheet/app/sheet.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/shticell";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PATH = FULL_SERVER_PATH + "/login";
    public final static String LOAD_SHEET_PATH = FULL_SERVER_PATH + "/loadSheet";
    public final static String SHEET_LIST_PATH = FULL_SERVER_PATH + "/sheetsList";
    public final static String GET_SHEET_PATH = FULL_SERVER_PATH + "/getSheet";

    // Gson instance
    public final static Gson GSON_INSTANCE = new Gson();
}
