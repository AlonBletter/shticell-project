package engine.sheet.api;

import engine.sheet.cell.api.CellReadActions;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.range.Range;

import java.util.List;
import java.util.Map;

public interface SheetReadActions {
    CellReadActions getCell(Coordinate coordinate);
    String getName();
    int getRowHeightUnits();
    int getColumnWidthUnits();
    int getNumberOfRows();
    int getNumberOfColumns();
    Map<Coordinate, List<Coordinate>> getCellInfluenceOn();
    Map<Coordinate, List<Coordinate>> getCellDependsOn();
    int getVersionNumber();
    List<Coordinate> getRangeCellsCoordinates(String rangeNameToView);
    List<Range> getRanges();
    List<String> getColumnUniqueValues(String columnLetter);
}
