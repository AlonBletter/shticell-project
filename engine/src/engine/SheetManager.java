package engine;

import dto.sheet.cell.CellDTO;
import dto.sheet.range.RangeDTO;
import dto.sheet.SheetDTO;
import engine.sheet.api.SheetReadActions;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.range.Range;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SheetManager {
    void loadSystemSettingsFromFile(String filePath);
    void loadSystemSettingsFromFile(String uploadedBy, InputStream inputStream);
    SheetDTO getSpreadsheet();
    CellDTO getCell(Coordinate cellToGetCoordinate);
    SheetDTO updateCell(String modifiedBy, Coordinate cellToUpdateCoordinate, String newCellOriginalValue, int sheetVersionFromUser);
    void updateCellBackgroundColor(Coordinate cellToUpdateCoordinate, String backgroundColor, int sheetVersionFromUser);
    void updateCellTextColor(Coordinate cellToUpdateCoordinate, String textColor, int sheetVersionFromUser);
    int getCurrentVersionNumber();
    SheetDTO getSheetByVersion(int requestedVersionNumber);
    void writeSystemDataToFile(String filePath) throws IOException;
    void readSystemDataFromFile(String filePath) throws IOException, ClassNotFoundException;
    void addRange(String rangeName, String rangeCoordinates, int sheetVersionFromUser);
    void deleteRange(String rangeNameToDelete, int sheetVersionFromUser);
    RangeDTO getRange(String rangeNameToView);
    List<Range> getRanges();
    SheetDTO getSortedSheet(String rangeToSortBy, List<String> columnsToSortBy);
    SheetDTO getFilteredSheet(String rangeToFilter, Map<String, List<String>> filterRequestValues);
    SheetDTO getExpectedValue(Coordinate cellToCalculate, String newValueOfCell, int sheetVersionFromUser);
    List<Coordinate> getAxis(String axisRange);
    SheetReadActions getSheetReadActions();
}
