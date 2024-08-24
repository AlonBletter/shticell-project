package engine;

import dto.CellDTO;
import dto.SheetDTO;
import engine.exception.DataReadException;
import engine.sheet.coordinate.Coordinate;

import java.io.IOException;

public interface Engine {
    void loadSystemSettingsFromFile(String filePath) throws DataReadException;
    SheetDTO getSpreadsheet();
    CellDTO getCell(Coordinate cellToGetCoordinate);
    void updateCell(Coordinate cellToUpdateCoordinate, String newCellOriginalValue);
    void writeSystemDataToFile(String filePath) throws IOException;
    void readSystemDataFromFile(String filePath) throws IOException, ClassNotFoundException;
}
