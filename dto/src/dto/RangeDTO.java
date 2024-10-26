package dto;

import engine.sheet.coordinate.Coordinate;
import engine.sheet.range.Range;

import java.util.LinkedList;
import java.util.List;

public record RangeDTO(
        String name,
        Coordinate start,
        Coordinate end,
        List<Coordinate> cellsInRange
) {
    public static RangeDTO convertToRangeDTO(Range range) {
        List<Coordinate> cellsInRange = new LinkedList<>(range.getCellsInRange());
        return new RangeDTO(
                range.getName(),
                range.getStart(),
                range.getEnd(),
                cellsInRange
        );
    }
}
