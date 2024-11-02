package engine.converter;

import dto.coordinate.Coordinate;
import dto.range.RangeDTO;
import engine.sheet.range.Range;

import java.util.LinkedList;
import java.util.List;

public class RangeConverter {
    public static RangeDTO convertToDTO(Range range) {
        List<Coordinate> cellsInRange = new LinkedList<>(range.getCellsInRange());
        return new RangeDTO(
                range.getName(),
                range.getStart(),
                range.getEnd(),
                cellsInRange
        );
    }
}
