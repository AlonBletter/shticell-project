package engine;

import dto.CellDTO;
import dto.SheetDTO;
import engine.exception.DataReadException;

public interface Engine {
    void loadSystemSettingsFromFile(String filePath) throws DataReadException;
    SheetDTO getSpreadsheet();
    CellDTO getCell(int row, int column);
    void updateCell(CellDTO updatedCell);
}
