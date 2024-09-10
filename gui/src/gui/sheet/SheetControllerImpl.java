package gui.sheet;

import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import gui.ShticellResourcesConstants;
import gui.app.AppController;
import gui.singlecell.SingleCellController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;

public class SheetControllerImpl implements SheetController {

    private AppController mainController;
    private GridPane centerGrid;
    private UIModel uiModel;

    private void initialize(SheetDTO sheet) {

        // update cell data through the ui model
        uiModel = new UIModel(sheet);

        // selected cell listener
        selectedCell = new SimpleObjectProperty<>();
        selectedCell.addListener((observableValue, oldLabelSelection, newSelectedLabel) -> {
            if (oldLabelSelection != null) {
                oldLabelSelection.setId(null);
            }
            if (newSelectedLabel != null) {
                newSelectedLabel.setId("selected-cell");
            }
        });
        addClickEventForCell(cellA1);
        addClickEventForCell(cellA2);
        addClickEventForCell(cellB1);
        addClickEventForCell(cellB2);
    }

    public CenterController() {
        this.gridCells = FXCollections.observableMap(new HashMap<>());
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public GridPane getCenterGrid() {
        return centerGrid;
    }

    public void initializeGrid(SheetDTO sheet) {
        int numOfRows = sheet.numOfRows();
        int numOfColumns = sheet.numOfColumns();
        int rowsUnits = sheet.rowHeightUnits();
        int columnUnits = sheet.columnWidthUnits();

        centerGrid = new GridPane();
        centerGrid.getChildren().clear();
        centerGrid.setHgap(1);
        centerGrid.setVgap(1);
        
        changeRowsConstraints(numOfRows, rowsUnits);
        changeColumnsConstraints(numOfColumns, columnUnits);

        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(ShticellResourcesConstants.CELL_FXML_URL);
                    Node singleCell = loader.load();
                    SingleCellController cellController = loader.getController();

                    Coordinate coordinate = CoordinateFactory.createCoordinate(row + 1, col + 1);
                    CellDTO cell = sheet.activeCells().get(coordinate);


                    cellController.setValue(valueString);
                    cellController.setCoordinate(coordinate);
                    cellController.setMainController(mainController);
                    gridCells.put(coordinate, cellController);

                    centerGrid.add(singleCell, col, row);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void changeColumnsConstraints(int numOfColumns, int columnUnits) {
        for (int col = 0; col < numOfColumns; col++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(columnUnits);
            colConstraints.setMaxWidth(columnUnits);
            colConstraints.setPrefWidth(columnUnits);
            centerGrid.getColumnConstraints().add(colConstraints);
        }
    }

    private void changeRowsConstraints(int numOfRows, int rowsUnits) {
        for (int row = 0; row < numOfRows; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(rowsUnits);
            rowConstraints.setMaxHeight(rowsUnits);
            rowConstraints.setPrefHeight(rowsUnits);
            centerGrid.getRowConstraints().add(rowConstraints);
        }
    }

    @Override
    public void markCellsButtonActionListener(boolean isMarked) {

    }

    @Override
    public void updateCellContent(String cellId, String content) {

    }
}
