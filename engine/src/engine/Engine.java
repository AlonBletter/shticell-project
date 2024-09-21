package engine;

import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;

import java.io.IOException;
import java.util.List;

public interface Engine {
    void loadSystemSettingsFromFile(String filePath);
    SheetDTO getSpreadsheet();
    CellDTO getCell(Coordinate cellToGetCoordinate);
    void updateCell(Coordinate cellToUpdateCoordinate, String newCellOriginalValue);
    void updateCellBackgroundColor(Coordinate cellToUpdateCoordinate, String backgroundColor);
    void updateCellTextColor(Coordinate cellToUpdateCoordinate, String textColor);
    int getCurrentVersionNumber();
    SheetDTO getSheetByVersion(int requestedVersionNumber);
    void writeSystemDataToFile(String filePath) throws IOException;
    void readSystemDataFromFile(String filePath) throws IOException, ClassNotFoundException;
    void addRange(String rangeName, String rangeCoordinates);
    void deleteRange(String rangeNameToDelete);
    List<Coordinate> getRange(String rangeNameToView);
}
