package engine.sheet.sort.sortablerow;

import engine.sheet.cell.api.Cell;

import java.util.List;
import java.util.Map;

public class SortableRow {
    private final int originalRow;
    private final int startColumn;
    private final int endColumn;
    private final Map<Integer, Double> numericValues;
    private final List<Cell> cellsInRow;

public SortableRow(int originalRow, int startColumn, int endColumn, Map<Integer, Double> numericValues, List<Cell> cellsInRow) {
        this.originalRow = originalRow;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
        this.numericValues = numericValues;
        this.cellsInRow = cellsInRow;
    }

    public int getOriginalRow() {
        return originalRow;
    }

    public List<Cell> getCellsInRow() {
        return cellsInRow;
    }

    public Double getNumericValue(int column) {
        return numericValues.get(column);
    }

    public String getEffectiveValueString(int column) {
        return cellsInRow.get(column - startColumn).getEffectiveValue().getValue().toString();
    }
}
