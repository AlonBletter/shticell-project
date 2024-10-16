package client.component.sheet.center;

import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import client.component.sheet.app.SheetController;
import client.component.sheet.center.singlecell.SingleCellController;
import client.component.sheet.common.ShticellResourcesConstants;
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
import java.net.URL;
import java.util.*;

public class CenterController {
    private final static int ROW_AND_COLUMN_SIZE = 30;
    private SheetController mainController;
    private GridPane centerGrid;
    private final ObservableMap<Coordinate, SingleCellController> gridCells;
    private final List<SingleCellController> selectedCells;
    private final List<SingleCellController> selectedRange;
    private final Map<String, Label> columnTitles;
    private final Map<Integer, Label> rowTitles;
    private int numOfRows;
    private int numOfColumns;
    private final Map<Integer, Double> columnWidthUnits;
    private final Map<Integer, Double> rowHeightUnits;
    private final List<SingleCellController> currentDependsOn;
    private final List<SingleCellController> currentInfluenceOn;
    private boolean editable;

    public CenterController() {
        this.gridCells = FXCollections.observableMap(new HashMap<>());
        selectedCells = new LinkedList<>();
        selectedRange = new LinkedList<>();
        columnTitles = new HashMap<>();
        rowTitles = new HashMap<>();
        columnWidthUnits = new HashMap<>();
        rowHeightUnits = new HashMap<>();
        currentDependsOn = new LinkedList<>();
        currentInfluenceOn = new LinkedList<>();
    }

    public void setMainController(SheetController mainController) {
        this.mainController = mainController;
    }

    public GridPane getCenterGrid() {
        return centerGrid;
    }

    public void initializeGrid(SheetDTO sheet, boolean editable) {
        numOfRows = sheet.numOfRows();
        numOfColumns = sheet.numOfColumns();
        double rowHeightUnits = sheet.rowHeightUnits();
        double columnWidthUnits = sheet.columnWidthUnits();
        this.editable = editable;

        gridPaneReset(rowHeightUnits, columnWidthUnits);
        setRowsConstrains(rowHeightUnits);
        setColumnsConstraints(columnWidthUnits);
        initializeRowsTitle(rowHeightUnits);
        initializeColumnsTitle(columnWidthUnits);

        for (int row = 1; row <= numOfRows; row++) {
            for (int col = 1; col <= numOfColumns; col++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);
                CellDTO cell = sheet.activeCells().get(coordinate);
                addSingleCell(coordinate, cell);
            }
        }

        if (ShticellResourcesConstants.DEFAULT_CENTER_CSS_URL != null) {
            centerGrid.getStylesheets().add(ShticellResourcesConstants.DEFAULT_CENTER_CSS_URL.toExternalForm());
        } else {
            System.err.println("CSS resource not found: " + ShticellResourcesConstants.DEFAULT_CENTER_CSS_RESOURCE_IDENTIFIER);
        }

        gridCells.get(CoordinateFactory.createCoordinate(1, 1)).onCellClickUpdate(null);
    }

    public void setSkin(String skinType) {
        switch (skinType) {
            case "Blue":
                applyCSS(Objects.requireNonNull(ShticellResourcesConstants.BLUE_CENTER_CSS_URL));
                break;
            case "Red":
                applyCSS(Objects.requireNonNull(ShticellResourcesConstants.RED_CENTER_CSS_URL));
                break;
            case "Default":
                applyCSS(Objects.requireNonNull(ShticellResourcesConstants.DEFAULT_CENTER_CSS_URL));
                break;
        }

        setSkinsForCells(skinType);
    }

    private void applyCSS(URL cssURL) {
        centerGrid.getStylesheets().clear();
        centerGrid.getStylesheets().add(cssURL.toExternalForm());
    }

    private void setSkinsForCells(String skinType) {
        for (int row = 1; row <= numOfRows; row++) {
            for (int col = 1; col <= numOfColumns; col++) {
                gridCells.get(CoordinateFactory.createCoordinate(row, col)).setSkin(skinType);
            }
        }
    }

    private void addSingleCell(Coordinate coordinate, CellDTO cell) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ShticellResourcesConstants.CELL_FXML_URL);
            Node singleCell = loader.load();
            SingleCellController cellController = loader.getController();

            cellController.setDataFromDTO(coordinate, cell);
            cellController.setMainController(mainController);
            cellController.setCenterController(this);
            cellController.setEditable(editable);

            gridCells.put(coordinate, cellController);
            centerGrid.add(singleCell, coordinate.column(), coordinate.row());
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred...", e);
        }
    }

    public void updateCellBackgroundColor(Coordinate coordinate, String newColor) {
        SingleCellController cellController = gridCells.get(coordinate);
        if (cellController != null) {
            cellController.updateBackgroundColor(newColor);
        }
    }

    public void updateCellTextColor(Coordinate coordinate, String newColor) {
        SingleCellController cellController = gridCells.get(coordinate);
        if (cellController != null) {
            cellController.updateTextColor(newColor);
        }
    }

    private void gridPaneReset(double rowHeightUnits, double columnWidthUnits) {
        centerGrid = new GridPane();
        centerGrid.setGridLinesVisible(true);
        centerGrid.getChildren().clear();
        centerGrid.setMinWidth((columnWidthUnits*numOfColumns) + ROW_AND_COLUMN_SIZE);
        centerGrid.setMinHeight((rowHeightUnits*numOfRows) + ROW_AND_COLUMN_SIZE);
        centerGrid.getStyleClass().add("grid");
    }

    private void initializeRowsTitle(double rowHeightUnitsValue) {
        for (int row = 1; row <= numOfRows; row++) {
            Label rowTitle = new Label(String.valueOf(row));
            rowTitle.getStyleClass().add("row-title");
            rowTitle.setMinHeight(0);
            rowTitle.setMaxHeight(rowHeightUnitsValue);
            rowTitle.setPrefHeight(rowHeightUnitsValue);
            rowTitle.setPrefWidth(ROW_AND_COLUMN_SIZE);
            if (editable) {
                final int finalRow = row;
                rowTitle.setOnMouseClicked(e -> selectRow(finalRow));
            }
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

            if (editable) {
                final int finalCol = col;
                columnTitle.setOnMouseClicked(e -> selectColumn(finalCol));
            }
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
        unmarkRange();
    }

    public void updateDependenciesAndInfluences(List<Coordinate> dependencies, List<Coordinate> influences) {
        for(SingleCellController cellController : currentDependsOn) {
            cellController.getCellNode().getStyleClass().remove("depends-on-cell");
        }
        currentDependsOn.clear();

        for(SingleCellController cellController : currentInfluenceOn) {
            cellController.getCellNode().getStyleClass().remove("influence-on-cell");
        }
        currentInfluenceOn.clear();

        for(Coordinate coordinate : dependencies) {
            SingleCellController currentCell = gridCells.get(coordinate);
            currentCell.getCellNode().getStyleClass().add("depends-on-cell");
            currentDependsOn.add(currentCell);
        }

        for(Coordinate coordinate : influences) {
            SingleCellController currentCell = gridCells.get(coordinate);
            currentCell.getCellNode().getStyleClass().add("influence-on-cell");
            currentInfluenceOn.add(currentCell);
        }
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

    // The problem with the influence cells mark of b2 \ c2 in 1-insurance is that we receive only the modified cells,
    // hence when updating d2 we don't get b2 and c2 for the setDataFromDTO... only D2 was modified...
    // temporary solution
    public void updateCells(SheetDTO sheetDTO) {
        for(int row = 1; row <= numOfRows; row++) {
            for(int col = 1; col <= numOfColumns; col++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);
                SingleCellController cellController = gridCells.get(coordinate);
                cellController.setDataFromDTO(coordinate, sheetDTO.activeCells().get(coordinate));
                updateDependenciesAndInfluences(cellController.getDependsOn(), cellController.getInfluenceOn());
            }
        }
    }

    public void updateColumnWidth(int columnIndex, double newWidth) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(newWidth);
        columnConstraints.setMaxWidth(newWidth);
        columnConstraints.setPrefWidth(newWidth);

        centerGrid.getColumnConstraints().set(columnIndex, columnConstraints);
        columnTitles.get(getColumnLetter(columnIndex)).setMinWidth(newWidth);
        columnWidthUnits.put(columnIndex, newWidth);
    }

    public void updateRowHeight(int rowIndex, double newHeight) {
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(newHeight);
        rowConstraints.setMaxHeight(newHeight);
        rowConstraints.setPrefHeight(newHeight);

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

    public void markRange(List<Coordinate> cellsInRange) {
        unmarkRange();
        for(Coordinate coordinate : cellsInRange) {
            SingleCellController cellController = gridCells.get(coordinate);
            cellController.getCellNode().getStyleClass().add("range-mark");
            selectedRange.add(cellController);
        }
    }

    public void unmarkRange() {
        for(SingleCellController cellController : selectedRange) {
            cellController.getCellNode().getStyleClass().removeAll("range-mark");
        }
        selectedRange.clear();
    }

    public void refreshExpectedValues(SheetDTO modifiedCells) {
        for(Map.Entry<Coordinate, CellDTO> entry : modifiedCells.activeCells().entrySet()) {
            SingleCellController cellController = gridCells.get(entry.getKey());
            cellController.setExpectedValue(modifiedCells.activeCells().get(entry.getKey()).effectiveValue());
        }
    }

    public void restoreCurrentValues() {
        for(int row = 1; row <= numOfRows; row++) {
            for(int col = 1; col <= numOfColumns; col++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);
                SingleCellController cellController = gridCells.get(coordinate);
                cellController.restoreEffectiveValue();
            }
        }
    }
}
