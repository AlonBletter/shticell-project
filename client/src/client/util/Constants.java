package client.util;

public class Constants {


    // FXML resources locations
    public final static String DASHBOARD_FXML_RESOURCE_LOCATION = "/client/component/dashboard/dashboard.fxml";
    public final static String MAIN_SCREEN_FXML_RESOURCE_LOCATION = "/client/component/main/app.fxml";
    public final static String LOGIN_SCREEN_FXML_RESOURCE_LOCATION = "/client/component/login/login.fxml";



    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/shticell";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
}
