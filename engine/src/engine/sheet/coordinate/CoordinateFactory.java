package engine.sheet.coordinate;

import com.sun.istack.NotNull;

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
        return createCoordinate(column + row);
    }

    public static Coordinate createCoordinate(String cellCoordinate) {
        if (cellCoordinate == null || cellCoordinate.isEmpty()) {
            throw new IllegalArgumentException("Cell coordinate cannot be null or empty.");
        }

        if (!Character.isLetter(cellCoordinate.charAt(0))) {
            throw new IllegalArgumentException("Invalid cell coordinate: " + cellCoordinate + ". Expected format like 'A1'.");
        }

        int parsedColumn = cellCoordinate.charAt(0) - 'A' + 1;
        int parsedRow;

        try {
            parsedRow = Integer.parseInt(cellCoordinate.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid cell coordinate: " + cellCoordinate + ". Row part must be a number.", e);
        }
        return createCoordinate(parsedRow, parsedColumn);
    }
}
