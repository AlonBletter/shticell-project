package client.util.http;

import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.range.Range;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface SheetService {
    SheetDTO getSpreadsheet();
    CellDTO getCell(Coordinate cellToGetCoordinate);
    void updateCell(Coordinate cellToUpdateCoordinate, String newCellOriginalValue, Consumer<SheetDTO> updateSheet);
    void updateCellBackgroundColor(Coordinate cellToUpdateCoordinate, String backgroundColor);
    void updateCellTextColor(Coordinate cellToUpdateCoordinate, String textColor);
    int getCurrentVersionNumber();
    void getSheetByVersion(int requestedVersionNumber, Consumer<SheetDTO> displaySheet);
    void addRange(String rangeName, String rangeCoordinates);
    void deleteRange(String rangeNameToDelete);
    List<Coordinate> getRange(String rangeNameToView);
    List<Range> getRanges();
    SheetDTO getSortedSheet(String rangeToSortBy, List<String> columnsToSortBy);
    List<String> getColumnUniqueValue(String columnLetter);
    SheetDTO getFilteredSheet(String rangeToFilter, Map<String, List<String>> filterRequestValues);
    SheetDTO getExpectedValue(Coordinate cellToCalculate, String newValueOfCell);
    List<Coordinate> getAxis(String axisRange);
}
