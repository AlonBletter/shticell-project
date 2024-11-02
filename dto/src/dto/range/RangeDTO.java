package dto.range;

import dto.coordinate.Coordinate;

import java.util.List;

public record RangeDTO(
        String name,
        Coordinate start,
        Coordinate end,
        List<Coordinate> cellsInRange
) {
}
