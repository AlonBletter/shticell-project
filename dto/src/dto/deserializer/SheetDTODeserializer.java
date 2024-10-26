package dto.deserializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dto.CellDTO;
import dto.RangeDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateImpl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SheetDTODeserializer implements JsonDeserializer<SheetDTO> {

    @Override
    public SheetDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        int version = jsonObject.get("versionNumber").getAsInt();
        int rows = jsonObject.get("numOfRows").getAsInt();
        int columns = jsonObject.get("numOfColumns").getAsInt();
        int rowHeightUnits = jsonObject.get("rowHeightUnits").getAsInt();
        int columnWidthUnits = jsonObject.get("columnWidthUnits").getAsInt();

        Map<Coordinate, CellDTO> activeCells = new HashMap<>();
        JsonObject cellsJson = jsonObject.get("activeCells").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : cellsJson.entrySet()) {
            Coordinate coordinate = deserializeCoordinate(entry.getKey());
            CellDTO cellDTO = context.deserialize(entry.getValue(), CellDTO.class);
            activeCells.put(coordinate, cellDTO);
        }

        Map<Coordinate, List<Coordinate>> cellDependents = deserializeAdjacencyList(
                jsonObject.get("cellDependents").getAsJsonObject(), context);

        Map<Coordinate, List<Coordinate>> cellReferences = deserializeAdjacencyList(
                jsonObject.get("cellReferences").getAsJsonObject(), context);

        Type listOfCellDTOType = new TypeToken<List<CellDTO>>() {}.getType();
        List<CellDTO> lastModifiedCells = context.deserialize(
                jsonObject.get("lastModifiedCells").getAsJsonArray(), listOfCellDTOType);

        List<RangeDTO> ranges = new LinkedList<>();
        JsonArray rangesJsonArray = jsonObject.get("ranges").getAsJsonArray();

        for (JsonElement element : rangesJsonArray) {
            RangeDTO range = context.deserialize(element, RangeDTO.class);
            ranges.add(range);
        }

        return new SheetDTO(
                name,
                rows,
                columns,
                rowHeightUnits,
                columnWidthUnits,
                activeCells,
                cellDependents,
                cellReferences,
                lastModifiedCells,
                version,
                ranges
        );
    }

    private Coordinate deserializeCoordinate(String coordinateStr) {
        int row = Integer.parseInt(coordinateStr.substring(1));
        int column = coordinateStr.charAt(0) - 'A' + 1;
        return new CoordinateImpl(row, column);
    }

    private Map<Coordinate, List<Coordinate>> deserializeAdjacencyList(JsonObject adjacencyListJson, JsonDeserializationContext context) {
        Map<Coordinate, List<Coordinate>> adjacencyList = new HashMap<>();
        Type listOfCoordinatesType = new TypeToken<List<Coordinate>>() {}.getType();
        for (Map.Entry<String, JsonElement> entry : adjacencyListJson.entrySet()) {
            Coordinate key = deserializeCoordinate(entry.getKey());
            List<Coordinate> value = context.deserialize(entry.getValue(), listOfCoordinatesType);
            adjacencyList.put(key, value);
        }
        return adjacencyList;
    }
}
