package engine;

import dto.CellDTO;
import dto.SpreadsheetDTO;

public interface Engine {
    SpreadsheetDTO getSpreadsheet();
    CellDTO getCell(int row, int column);
    void updateCell(CellDTO updatedCell);
}
