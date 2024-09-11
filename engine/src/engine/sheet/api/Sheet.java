package engine.sheet.api;

import engine.generated.STLSheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.api.CellReadActions;
import engine.sheet.coordinate.Coordinate;

import java.util.List;
import java.util.Map;

public interface Sheet extends SheetReadActions, SheetUpdateActions {
    Map<Coordinate, Cell> getActiveCells();
    List<Cell> getLastModifiedCells();
    Sheet copySheet();
}
