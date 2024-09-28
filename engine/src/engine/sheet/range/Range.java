package engine.sheet.range;

import engine.sheet.coordinate.Coordinate;

import java.util.List;

public interface Range {
    List<Coordinate> getCellsInRange();
    String getName();
}
