package engine.sheet.api;

import engine.generated.STLSheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;

import java.util.List;
import java.util.Map;

public interface Sheet { //TODO separate to read/update interfaces (also in cell)
    int getVersion();
    Cell getCell(Coordinate coordinate);
    void updateCell(Coordinate coordinate, String value);
    void setName(String name);
    void setNumberOfRows(int numberOfRows);
    void setNumberOfColumns(int numberOfColumns);
    void setRowHeightUnits(int rowHeightUnits);
    void setColumnWidthUnits(int columnWidthUnits);
    String getName();
    int getRowHeightUnits();
    int getColumnWidthUnits();
    int getNumberOfRows();
    int getNumberOfColumns();
    Map<Coordinate, Cell> getActiveCells();
    Map<Coordinate, List<Coordinate>> getCellDependents();
    Map<Coordinate, List<Coordinate>> getCellReferences();
    void init(STLSheet sheetToInitFrom);
}
