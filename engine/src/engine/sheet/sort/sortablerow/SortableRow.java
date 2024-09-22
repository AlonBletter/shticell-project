package engine.sheet.sort.sortablerow;

import engine.sheet.cell.api.Cell;

import java.util.List;
import java.util.Map;

public class SortableRow {
    private final int originalRow;
    private final Map<Integer, Double> values;
    private final List<Cell> cellsInRow;

    public SortableRow(int originalRow, Map<Integer, Double> values, List<Cell> cellsInRow) {
        this.originalRow = originalRow;
        this.values = values;
        this.cellsInRow = cellsInRow;
    }

    public int getOriginalRow() {
        return originalRow;
    }

//    public List<Double> getValues() {
//        return values;
//    }

    public List<Cell> getCellsInRow() {
        return cellsInRow;
    }

    public Double getValueFromColumn(int column) {
        return values.get(column);
    }
}
