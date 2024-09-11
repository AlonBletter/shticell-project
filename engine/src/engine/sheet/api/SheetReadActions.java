package engine.sheet.api;

import engine.sheet.cell.api.Cell;
import engine.sheet.cell.api.CellReadActions;
import engine.sheet.coordinate.Coordinate;

import java.util.List;
import java.util.Map;

public interface SheetReadActions {
    CellReadActions getCell(Coordinate coordinate);
    String getName();
    int getRowHeightUnits();
    int getColumnWidthUnits();
    int getNumberOfRows();
    int getNumberOfColumns();
    Map<Coordinate, List<Coordinate>> getCellDependents();
    Map<Coordinate, List<Coordinate>> getCellReferences();
    int getVersionNumber();
}
