package engine.sheet.range;

import engine.expression.ExpressionUtils;
import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RangeImpl implements Range, Serializable {
    private String name;
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

        int startRow = start.getRow();
        int startCol = start.getColumn();
        int endRow = end.getRow();
        int endCol = end.getColumn();

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

    public List<Coordinate> getCellsInRange() {
        return new LinkedList<>(cellsInRange);
    }
}
