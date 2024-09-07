package gui.center;

import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.effectivevalue.EffectiveValue;
import gui.ShticellResourcesConstants;
import gui.app.AppController;
import gui.singlecell.CellModel;
import gui.singlecell.SingleCellController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.util.HashMap;

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

        centerGrid = new GridPane();
        centerGrid.getChildren().clear();
        centerGrid.setHgap(1);
        centerGrid.setVgap(1);

        for (int row = 0; row < numOfRows; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(rowsUnits);
            rowConstraints.setMaxHeight(rowsUnits);
            rowConstraints.setPrefHeight(rowsUnits);
            centerGrid.getRowConstraints().add(rowConstraints);
        }

        for (int col = 0; col < numOfColumns; col++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(columnUnits);
            colConstraints.setMaxWidth(columnUnits);
            colConstraints.setPrefWidth(columnUnits);
            centerGrid.getColumnConstraints().add(colConstraints);
        }

        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(ShticellResourcesConstants.CELL_FXML_URL);
                    Node singleCell = loader.load();
                    SingleCellController cellController = loader.getController();

                    Coordinate coordinate = CoordinateFactory.createCoordinate(row + 1, col + 1);
                    CellDTO cell = sheet.activeCells().get(coordinate);
                    String valueString = (cell != null ? formatValueFromSheet(cell.effectiveValue()) : "");

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
    private String formatValueFromSheet(EffectiveValue objectFromSheet) {
        String formattedObject = objectFromSheet.getValue().toString();

        if(CellType.NUMERIC == objectFromSheet.getCellType()) {
            double doubleValue = (Double) objectFromSheet.getValue();

            if(doubleValue % 1 == 0) {
                long longValue = (long) doubleValue;

                formattedObject = String.format("%,d", longValue);
            } else {
                formattedObject = String.format("%,.2f", doubleValue);
            }
        } else if(CellType.BOOLEAN == objectFromSheet.getCellType()) {
            boolean booleanValue = (Boolean) objectFromSheet.getValue();

            formattedObject = Boolean.toString(booleanValue).toUpperCase();
        } else if (CellType.EMPTY == objectFromSheet.getCellType()) {
            formattedObject = "";
        }

        return formattedObject;
    }

}
