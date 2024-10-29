package server.constants;

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
    public static final String USERNAME = "username";
    public static final String SHEET_NAME = "sheetName";
    public static final String VERSION = "version";
    public static final String RANGE = "range";
    public static final String USER_NAME_ERROR = "username_error";




    public static final String CHAT_PARAMETER = "userstring";
    public static final String CHAT_VERSION_PARAMETER = "chatversion";
    
    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;

    public static final Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
            .registerTypeAdapter(CellStyleDTO.class, new CellStyleDTOAdapter())
            .registerTypeAdapter(Coordinate.class, new CoordinateTypeAdapter())
            .registerTypeAdapter(EffectiveValue.class ,new EffectiveValueTypeAdapter())
            .create();
}