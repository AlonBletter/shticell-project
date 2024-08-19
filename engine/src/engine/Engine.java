package engine;

import dto.CellDTO;
import dto.SheetDTO;
import engine.exception.DataReadException;
import engine.sheet.coordinate.Coordinate;

public interface Engine {
    void loadSystemSettingsFromFile(String filePath) throws DataReadException;
    SheetDTO getSpreadsheet();
    CellDTO getCell(Coordinate cellToGetCoordinate);
    void updateCell(String cellToUpdate, String newCellOriginalValue);
}
