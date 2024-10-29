package client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.sheet.cell.CellStyleDTO;
import dto.sheet.SheetDTO;
import dto.adapter.CellStyleDTOAdapter;
import dto.adapter.CoordinateTypeAdapter;
import dto.adapter.EffectiveValueTypeAdapter;
import dto.adapter.SheetDTODeserializer;
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
    public final static String UPDATE_CELL_BACKGROUND_COLOR_PATH = FULL_SERVER_PATH + "/sheet/cell/background/color";
    public final static String UPDATE_CELL_TEXT_COLOR_PATH = FULL_SERVER_PATH + "/sheet/cell/text/color";
    public final static String GET_SORTED_SHEET_PATH = FULL_SERVER_PATH + "/sheet/sort";
    public final static String ADD_RANGE_PATH = FULL_SERVER_PATH + "/sheet/range/add";
    public final static String DELETE_RANGE_PATH = FULL_SERVER_PATH + "/sheet/range/delete";
    public final static String GET_EXPECTED_VALUE_PATH = FULL_SERVER_PATH + "/sheet/whatIf";
    public final static String GET_FILTERED_SHEET_PATH = FULL_SERVER_PATH + "/sheet/filter";
    public final static String GET_AXIS_PATH = FULL_SERVER_PATH + "/sheet/axis";
    public final static String GET_LATEST_SHEET_VERSION_PATH = FULL_SERVER_PATH + "/sheet/version/last";
    public final static String GET_VERSION_OF_SHEET_PATH = FULL_SERVER_PATH + "/sheet/version/last/number";

    // Gson instance
    public final static Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
            .registerTypeAdapter(CellStyleDTO.class, new CellStyleDTOAdapter())
            .registerTypeAdapter(Coordinate.class, new CoordinateTypeAdapter())
            .registerTypeAdapter(EffectiveValue.class ,new EffectiveValueTypeAdapter())
            .serializeSpecialFloatingPointValues()
            .create();
}
