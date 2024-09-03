package gui.model;


import engine.sheet.coordinate.Coordinate;
import gui.singlecell.CellModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dto.SheetDTO;
import dto.CellDTO;
import javafx.collections.ObservableMap;

public class SheetModel {
    private ObservableMap<Coordinate, CellModel> activeCells;
    private SheetDTO sheetDTO;

    public SheetModel(SheetDTO sheetDTO) {
        this.sheetDTO = sheetDTO;
        /*initialize(sheetDTO);*/
    }

/*    private void initialize(SheetDTO sheetDTO) {
        rows = FXCollections.observableArrayList();
        for (int i = 0; i < sheetDTO.numOfRows(); i++) {
            ObservableList<CellModel> row = FXCollections.observableArrayList();
            for (CellDTO cellDTO : sheetDTO.activeCells().get(i)) {
                row.add(new CellModel(cellDTO));
            }

            rows.add(row);
        }
    }

    public ObservableList<ObservableList<CellModel>> getRows() {
        return rows;
    }

    // Method to update data from engine
    public void updateData(SheetDTO newSheetDTO) {
        this.sheetDTO = newSheetDTO;
        initialize(newSheetDTO);
        // Notify listeners or update view if needed
    }*/
}