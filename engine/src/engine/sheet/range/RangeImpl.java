package engine.sheet.range;

import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class RangeImpl implements Range, Serializable {
    private final String name;
    private final Coordinate start;
    private final Coordinate end;
    private List<Coordinate> cellsInRange;

    public RangeImpl(String name, Coordinate start, Coordinate end) {
        this.name = name;
        this.start = start;
        this.end = end;
        initializeCellsInRange();
    }

    private void initializeCellsInRange() {
        cellsInRange = new LinkedList<>();

        int startRow = start.row();
        int startCol = start.column();
        int endRow = end.row();
        int endCol = end.column();

        if (endRow < startRow || endCol < startCol) {
            throw new IllegalArgumentException("Invalid range: The bottom-right coordinate [" + end + "]" +
                    " cannot be above or to the left of the top-left coordinate [" + start + "].");
        }

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                Coordinate cellCoordinate = CoordinateFactory.createCoordinate(row, col);
                cellsInRange.add(cellCoordinate);
            }
        }
    }

    @Override
    public List<Coordinate> getCellsInRange() {
        return new LinkedList<>(cellsInRange);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Coordinate getStart() {
        return start;
    }

    @Override
    public Coordinate getEnd() {
        return end;
    }
}
