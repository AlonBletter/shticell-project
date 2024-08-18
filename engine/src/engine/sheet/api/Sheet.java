package engine.sheet.api;

import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;

import java.util.Map;

public interface Sheet {
    int getVersion();
    Cell getCell(Coordinate coordinate);
    void updateCell(Coordinate coordinate, String value);
    void setName(String name);
    void setRowHeightUnits(int rowHeightUnits);
    void setColumnWidthUnits(int columnWidthUnits);
    void setNumberOfRows(int numberOfRows);
    void setNumberOfColumns(int numberOfColumns);
    String getName();
    int getNumberOfRows();
    int getNumberOfColumns();
    int getRowHeightUnits();
    int getColumnWidthUnits();
    Map<Coordinate, Cell> getActiveCells();
    Map<Coordinate, Coordinate> getDependentCells();
    void resetActiveCells();
}
