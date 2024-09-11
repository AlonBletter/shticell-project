package gui.center;

import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import gui.ShticellResourcesConstants;
import gui.app.AppController;
import gui.singlecell.SingleCellController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CenterController {
    private AppController mainController;
    private GridPane centerGrid;
    private ObservableMap<Coordinate, SingleCellController> gridCells;

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

        gridPaneReset();
        setRowsConstrains(numOfRows + 1, rowsUnits);
        setColumnsConstraints(numOfColumns + 1, columnUnits);
        insertColumnsTitle(numOfColumns);
        insertRowsTitle(numOfRows);

        // Add grid cells
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(ShticellResourcesConstants.CELL_FXML_URL);
                    Node singleCell = loader.load();
                    SingleCellController cellController = loader.getController();

                    Coordinate coordinate = CoordinateFactory.createCoordinate(row + 1, col + 1);
                    CellDTO cell = sheet.activeCells().get(coordinate);
                    cellController.setDataFromDTO(coordinate, cell);
                    cellController.setMainController(mainController);
                    gridCells.put(coordinate, cellController);

                    // Add cell to grid, shifting by 1 to account for titles
                    centerGrid.add(singleCell, col + 1, row + 1); // Shift by 1
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        centerGrid.getStylesheets().add(getClass().getResource("/gui/center/center.css").toExternalForm());
    }

    private void gridPaneReset() {
        centerGrid = new GridPane();
        centerGrid.getChildren().clear();
        centerGrid.setHgap(1);
        centerGrid.setVgap(1);
    }

    private void insertRowsTitle(int numOfRows) {
        for (int row = 1; row <= numOfRows; row++) {
            Label rowTitle = new Label(String.valueOf(row));
            rowTitle.getStyleClass().add("column-title");
            centerGrid.add(rowTitle, 0, row);
            GridPane.setHalignment(rowTitle, HPos.RIGHT);
            GridPane.setValignment(rowTitle, VPos.CENTER);
        }
    }

    private void insertColumnsTitle(int numOfColumns) {
        for (int col = 1; col <= numOfColumns; col++) {
            Label columnTitle = new Label(getColumnLetter(col));
            columnTitle.getStyleClass().add("column-title");
            centerGrid.add(columnTitle, col, 0);
            GridPane.setHalignment(columnTitle, HPos.CENTER);
            GridPane.setValignment(columnTitle, VPos.CENTER);
        }
    }

    private String getColumnLetter(int columnNumber) {
        return String.valueOf((char) ('A' + columnNumber - 1));
    }

    private void setColumnsConstraints(int numOfColumns, int columnUnits) {
        for (int col = 0; col < numOfColumns; col++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(columnUnits);
            colConstraints.setMaxWidth(columnUnits);
            colConstraints.setPrefWidth(columnUnits);
            centerGrid.getColumnConstraints().add(colConstraints);
        }
    }

    private void setRowsConstrains(int numOfRows, int rowsUnits) {
        for (int row = 0; row < numOfRows; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(rowsUnits);
            rowConstraints.setMaxHeight(rowsUnits);
            rowConstraints.setPrefHeight(rowsUnits);
            centerGrid.getRowConstraints().add(rowConstraints);
        }
    }

    public void updateCells(List<CellDTO> cellDTOS) {
        if(!cellDTOS.isEmpty()) {
            for(CellDTO cellDTO : cellDTOS) {
                Coordinate coordinate = cellDTO.coordinate();
                SingleCellController cellController = gridCells.get(coordinate);
                cellController.setDataFromDTO(coordinate, cellDTO);
            }
        }
    }
}
