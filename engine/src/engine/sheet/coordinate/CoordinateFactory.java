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
        if (cellCoordinate == null ) {
            throw new IllegalArgumentException("Cell coordinate cannot be null.");
        } else if (cellCoordinate.isEmpty()) {
            throw new IllegalArgumentException("Cell coordinate cannot be empty.");
        }

        char columnLetter = Character.toUpperCase(cellCoordinate.charAt(0));

        if (!Character.isLetter(columnLetter)) {
            throw new IllegalArgumentException("Invalid cell coordinate: " + cellCoordinate + ".\n" +
                    "Expected format like 'A1' with a single letter for the column.");
        }

        int parsedColumn = columnLetter - 'A' + 1;
        int parsedRow;

        try {
            parsedRow = Integer.parseInt(cellCoordinate.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid cell coordinate: " + cellCoordinate + ".\n" +
                    "Expected format like 'A1' with a single letter for the column.", e);
        }

        return createCoordinate(parsedRow, parsedColumn);
    }
}
