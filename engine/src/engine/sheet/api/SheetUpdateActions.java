package engine.sheet.api;

import engine.generated.STLSheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;

import java.util.List;
import java.util.Map;

public interface SheetUpdateActions {
    boolean updateCell(Coordinate coordinate, String value);
    void setName(String name);
    void setNumberOfRows(int numberOfRows);
    void setNumberOfColumns(int numberOfColumns);
    void setRowHeightUnits(int rowHeightUnits);
    void setColumnWidthUnits(int columnWidthUnits);
    void init(STLSheet sheetToInitFrom);

    void updateCellBackgroundColor(Coordinate cellToUpdateCoordinate, String backgroundColor);
    void updateCellTextColor(Coordinate cellToUpdateCoordinate, String textColor);
    void addRange(String rangeName, String rangeCoordinates);
    void deleteRange(String rangeNameToDelete);
    void sort(String rangeToSortBy, List<String> columnsToSortBy);
}
