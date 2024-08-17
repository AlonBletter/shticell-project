package engine.sheet.coordinate;

import java.util.HashMap;
import java.util.Map;

public class CoordinateFactory {
    private static Map<String, Coordinate> cachedCoordinates = new HashMap<>();

    public static Coordinate createCoordinate(int row, int column) {

        String key = row + ":" + column;
        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }

        CoordinateImpl coordinate = new CoordinateImpl(row, column);
        cachedCoordinates.put(key, coordinate);

        return coordinate;
    }

    public static Coordinate createCoordinate(int row, String column) {
        int parsedColumn = column.charAt(0) - 'A' + 1;

        return createCoordinate(row, parsedColumn);
    }

    public static Coordinate createCoordinate(String cellCoordinate) {
        int parsedColumn = cellCoordinate.charAt(0) - 'A' + 1;
        int parsedRow = cellCoordinate.charAt(1) - '0' + 1;

        return createCoordinate(parsedRow, parsedColumn);
    }
}
