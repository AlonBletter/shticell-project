package engine;

import dto.CellDTO;
import dto.SheetDTO;
import dto.SheetInfoDTO;
import engine.sheet.api.SheetReadActions;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.range.Range;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SheetManager {
    void loadSystemSettingsFromFile(String filePath);
    void loadSystemSettingsFromFile(InputStream inputStream);
    SheetDTO getSpreadsheet();
    CellDTO getCell(Coordinate cellToGetCoordinate);
    SheetDTO updateCell(Coordinate cellToUpdateCoordinate, String newCellOriginalValue);
    void updateCellBackgroundColor(Coordinate cellToUpdateCoordinate, String backgroundColor);
    void updateCellTextColor(Coordinate cellToUpdateCoordinate, String textColor);

    int getCurrentVersionNumber();
    SheetDTO getSheetByVersion(int requestedVersionNumber);
    void writeSystemDataToFile(String filePath) throws IOException;
    void readSystemDataFromFile(String filePath) throws IOException, ClassNotFoundException;
    void addRange(String rangeName, String rangeCoordinates);
    void deleteRange(String rangeNameToDelete);
    List<Coordinate> getRange(String rangeNameToView);
    List<Range> getRanges();
    SheetDTO getSortedSheet(String rangeToSortBy, List<String> columnsToSortBy);
    List<String> getColumnUniqueValue(String columnLetter);
    SheetDTO getFilteredSheet(String rangeToFilter, Map<String, List<String>> filterRequestValues);
    SheetDTO getExpectedValue(Coordinate cellToCalculate, String newValueOfCell);

    List<Coordinate> getAxis(String axisRange);

    SheetReadActions getSheetReadActions();
}
