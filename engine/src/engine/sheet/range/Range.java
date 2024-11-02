package engine.sheet.range;

import dto.coordinate.Coordinate;

import java.util.List;

public interface Range {
    List<Coordinate> getCellsInRange();
    String getName();

    Coordinate getStart();

    Coordinate getEnd();
}
