package gui.sheet;

import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import gui.singlecell.CellModel;
import gui.singlecell.SingleCellController;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

public class UIModel {
    private Map<Coordinate, CellModel> cells;

    public UIModel(SheetDTO sheetDTO) {
        cells = new HashMap<>();

        for (Map.Entry<Coordinate, CellDTO> entry : sheetDTO.activeCells().entrySet()) {
            Coordinate coordinate = entry.getKey();
            CellDTO cellDTO = entry.getValue();
            cells.put(coordinate, new CellModel(cellDTO.effectiveValue(), coordinate));
        }
    }

    public Map<Coordinate, CellModel> getCells() {
        return cells;
    }

    public CellModel getCellModel(Coordinate coordinate) {
        return cells.get(coordinate);
    }
}
