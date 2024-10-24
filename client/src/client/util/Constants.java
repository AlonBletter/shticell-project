package client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CellStyleDTO;
import dto.SheetDTO;
import dto.deserializer.CellStyleDTOAdapter;
import dto.deserializer.CoordinateTypeAdapter;
import dto.deserializer.EffectiveValueTypeAdapter;
import dto.deserializer.SheetDTODeserializer;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;

public class Constants {

    public final static int REFRESH_RATE = 1000;

    // FXML resources locations
    public final static String MAIN_SCREEN_FXML_RESOURCE_LOCATION = "/client/component/main/app.fxml";
    public final static String LOGIN_SCREEN_FXML_RESOURCE_LOCATION = "/client/component/login/login.fxml";
    public final static String DASHBOARD_FXML_RESOURCE_LOCATION = "/client/component/dashboard/dashboard.fxml";
    public final static String SHEET_FXML_RESOURCE_LOCATION = "/client/component/sheet/app/sheet.fxml";
    public final static String PERMISSION_REQUEST_DIALOG_FXML_RESOURCE_LOCATION = "/client/component/dashboard/command/dialog/permissionRequest.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/shticell";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PATH = FULL_SERVER_PATH + "/login";
    public final static String LOAD_SHEET_PATH = FULL_SERVER_PATH + "/loadSheet";
    public final static String SHEET_LIST_PATH = FULL_SERVER_PATH + "/sheetsList";
    public final static String GET_SHEET_PATH = FULL_SERVER_PATH + "/getSheet";
    public final static String GET_PERMISSION_REQUESTS = FULL_SERVER_PATH + "/getPermissionRequests";
    public final static String REQUEST_PERMISSION = FULL_SERVER_PATH + "/requestPermission";
    public final static String PERMISSION_REQUEST_DECISION = FULL_SERVER_PATH + "/permission/request/decision";
    public final static String UPDATE_CELL_PATH = FULL_SERVER_PATH + "/sheet/update";
    public final static String GET_SHEET_BY_VERSION_PATH = FULL_SERVER_PATH + "/sheet/version/get";

    // Gson instance
    public final static Gson GSON_INSTANCE = new Gson(); //TODO redundant, can use the adapted only
    public final static Gson ADAPTED_GSON = new GsonBuilder()
            .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
            .registerTypeAdapter(CellStyleDTO.class, new CellStyleDTOAdapter())
            .registerTypeAdapter(Coordinate.class, new CoordinateTypeAdapter())
            .registerTypeAdapter(EffectiveValue.class ,new EffectiveValueTypeAdapter())
            .serializeSpecialFloatingPointValues()
            .create();
}
