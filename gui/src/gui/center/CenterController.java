package gui.center;

import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import gui.common.ShticellResourcesConstants;
import gui.app.AppController;
import gui.singlecell.SingleCellController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.util.*;

//TODO consider moving select to the app controller instead of here.

public class CenterController {
    private final static int ROW_AND_COLUMN_SIZE = 30;
    private AppController mainController;
    private GridPane centerGrid;
    private ObservableMap<Coordinate, SingleCellController> gridCells;
    private List<SingleCellController> selectedCells;
    private Map<String, Label> columnTitles;
    private Map<Integer, Label> rowTitles;
    private int numOfRows;
    private int numOfColumns;
    private Map<Integer, Double> columnWidthUnits;
    private Map<Integer, Double> rowHeightUnits;

    public CenterController() {
        this.gridCells = FXCollections.observableMap(new HashMap<>());
        selectedCells = new LinkedList<>();
        columnTitles = new HashMap<>();
        rowTitles = new HashMap<>();
        columnWidthUnits = new HashMap<>();
        rowHeightUnits = new HashMap<>();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public GridPane getCenterGrid() {
        return centerGrid;
    }

    public void initializeGrid(SheetDTO sheet) {
        numOfRows = sheet.numOfRows();
        numOfColumns = sheet.numOfColumns();
        double rowHeightUnits = sheet.rowHeightUnits();
        double columnWidthUnits = sheet.columnWidthUnits();

        gridPaneReset();
        setRowsConstrains(rowHeightUnits);
        setColumnsConstraints(columnWidthUnits);
        initializeRowsTitle(rowHeightUnits);
        initializeColumnsTitle(columnWidthUnits);

        for (int row = 1; row <= numOfRows; row++) {
            for (int col = 1; col <= numOfColumns; col++) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(ShticellResourcesConstants.CELL_FXML_URL);
                    Node singleCell = loader.load();
                    SingleCellController cellController = loader.getController();

                    Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);
                    CellDTO cell = sheet.activeCells().get(coordinate);
                    cellController.setDataFromDTO(coordinate, cell);
                    cellController.setMainController(mainController);
                    gridCells.put(coordinate, cellController);
                    centerGrid.add(singleCell, col, row);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        centerGrid.getStylesheets().add(getClass().getResource("/gui/center/center.css").toExternalForm());
        gridCells.get(CoordinateFactory.createCoordinate(1, 1)).onCellClickUpdate(null);
    }

    private void gridPaneReset() {
        centerGrid = new GridPane();
        centerGrid.getChildren().clear();
        centerGrid.setHgap(1);
        centerGrid.setVgap(1);
    }

    private void initializeRowsTitle(double rowHeightUnitsValue) {
        for (int row = 1; row <= numOfRows; row++) {
            Label rowTitle = new Label(String.valueOf(row));
            rowTitle.getStyleClass().add("row-title");
            rowTitle.setMinHeight(0);   // Match the cell height
            rowTitle.setMaxHeight(rowHeightUnitsValue);   // Keep the label's size consistent
            rowTitle.setPrefHeight(rowHeightUnitsValue);  // Ensure uniform height
            rowTitle.setPrefWidth(ROW_AND_COLUMN_SIZE);
            // Make row clickable to select
            final int finalRow = row;  // Use 'final' for the lambda
            rowTitle.setOnMouseClicked(e -> selectRow(finalRow));
            rowTitles.put(row, rowTitle);
            centerGrid.add(rowTitle, 0, row);
            GridPane.setHalignment(rowTitle, HPos.RIGHT);
            GridPane.setValignment(rowTitle, VPos.CENTER);
        }
    }

    private void initializeColumnsTitle(double columnWidthUnitsValue) {
        for (int col = 1; col <= numOfColumns; col++) {
            String columnLetter = getColumnLetter(col);
            Label columnTitle = new Label(columnLetter);
            columnTitle.getStyleClass().add("column-title");
            columnTitle.setMinWidth(0);
            columnTitle.setMaxWidth(columnWidthUnitsValue);
            columnTitle.setPrefWidth(columnWidthUnitsValue);
            columnTitle.setPrefHeight(ROW_AND_COLUMN_SIZE);

            final int finalCol = col;
            columnTitle.setOnMouseClicked(e -> selectColumn(finalCol));
            columnTitles.put(columnLetter, columnTitle);
            centerGrid.add(columnTitle, col, 0);
            GridPane.setHalignment(columnTitle, HPos.CENTER);
            GridPane.setValignment(columnTitle, VPos.CENTER);
        }
    }

    private void selectRow(int rowIndex) {
        gridCells.get(CoordinateFactory.createCoordinate(rowIndex, 1)).onCellClickUpdate(null);
        mainController.setSelectedRow(selectedCells);

        for (int col = 1; col <= numOfColumns; col++) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(rowIndex, col);
            SingleCellController cellController = gridCells.get(coordinate);
            if (cellController != null) {
                cellController.getCellNode().getStyleClass().add("selected-row");
                selectedCells.add(cellController);
            }
        }
    }
    private void selectColumn(int colIndex) {
        gridCells.get(CoordinateFactory.createCoordinate(1, colIndex)).onCellClickUpdate(null);
        mainController.setSelectedColumn(selectedCells);

        for (int row = 1; row <= numOfRows; row++) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(row, colIndex);
            SingleCellController cellController = gridCells.get(coordinate);
            if (cellController != null) {
                cellController.getCellNode().getStyleClass().add("selected-column");
                selectedCells.add(cellController);
            }
        }
    }

    public void clearSelection() {
        for (SingleCellController cellController : selectedCells) {
            cellController.getCellNode().getStyleClass().removeAll("selected-row", "selected-column");
        }

        selectedCells.clear();
    }

    private String getColumnLetter(int columnNumber) {
        return String.valueOf((char) ('A' + columnNumber - 1));
    }

    public double getRowHeight(int rowIndex) {
        return rowHeightUnits.get(rowIndex);
    }

    public double getColumnWidth(int columnNumber) {
        return columnWidthUnits.get(columnNumber);
    }

    public void setColumnsConstraints(double columnWidthUnitsValue) {
        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setMinWidth(0);
        colConstraints.setMaxWidth(columnWidthUnitsValue);
        colConstraints.setPrefWidth(columnWidthUnitsValue);

        for (int col = 0; col < numOfColumns + 1; col++) {
            centerGrid.getColumnConstraints().add(colConstraints);
            columnWidthUnits.put(col, columnWidthUnitsValue);
        }
        centerGrid.getColumnConstraints().set(0, new ColumnConstraints(ROW_AND_COLUMN_SIZE));
    }

    private void setRowsConstrains(double rowHeightUnitsValue) {
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(0);
        rowConstraints.setMaxHeight(rowHeightUnitsValue);
        rowConstraints.setPrefHeight(rowHeightUnitsValue);

        for (int row = 0; row < numOfRows + 1; row++) {
            centerGrid.getRowConstraints().add(rowConstraints);
            rowHeightUnits.put(row, rowHeightUnitsValue);
        }

        centerGrid.getRowConstraints().set(0, new RowConstraints(ROW_AND_COLUMN_SIZE));
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

    public void updateColumnWidth(int columnIndex, double newWidth) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(newWidth);
        columnConstraints.setMaxWidth(newWidth);
        columnConstraints.setPrefWidth(newWidth);

        // Update the specific column
        centerGrid.getColumnConstraints().set(columnIndex, columnConstraints);
        columnTitles.get(getColumnLetter(columnIndex)).setMinWidth(newWidth);
        columnWidthUnits.put(columnIndex, newWidth);
    }

    public void updateRowHeight(int rowIndex, double newHeight) {
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(newHeight);
        rowConstraints.setMaxHeight(newHeight);
        rowConstraints.setPrefHeight(newHeight);

        // Update the specific row
        centerGrid.getRowConstraints().set(rowIndex, rowConstraints);
        rowTitles.get(rowIndex).setMinHeight(newHeight);
        rowHeightUnits.put(rowIndex, newHeight);
    }

    public void alignColumnCells(int colIndex, Pos pos) {
        for (int row = 1; row <= numOfRows; row++) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(row, colIndex);
            SingleCellController cellController = gridCells.get(coordinate);
            cellController.setAlignment(pos);
        }
    }
}
