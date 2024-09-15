package gui.center;

import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import gui.common.ShticellResourcesConstants;
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
import java.util.LinkedList;
import java.util.List;

public class CenterController {
    private final static int ROW_AND_COLUMN_SIZE = 30;
    private AppController mainController;
    private GridPane centerGrid;
    private ObservableMap<Coordinate, SingleCellController> gridCells;
    private List<SingleCellController> selectedCells;
    private int numOfRows;
    private int numOfColumns;
    private int rowHeightUnits;
    private int columnWidthUnits;

    public CenterController() {
        this.gridCells = FXCollections.observableMap(new HashMap<>());
        selectedCells = new LinkedList<>();
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
        rowHeightUnits = sheet.rowHeightUnits();
        columnWidthUnits = sheet.columnWidthUnits();

        gridPaneReset();
        setRowsConstrains();
        setColumnsConstraints();
        initializeColumnsTitle();
        initializeRowsTitle();

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
    }

    private void gridPaneReset() {
        centerGrid = new GridPane();
        centerGrid.getChildren().clear();
        centerGrid.setHgap(1);
        centerGrid.setVgap(1);
    }

    private void initializeRowsTitle() {
        for (int row = 1; row <= numOfRows; row++) {
            Label rowTitle = new Label(String.valueOf(row));
            rowTitle.getStyleClass().add("row-title");
            rowTitle.setMinHeight(rowHeightUnits);   // Match the cell height
            rowTitle.setMaxHeight(rowHeightUnits);   // Keep the label's size consistent
            rowTitle.setPrefHeight(rowHeightUnits);  // Ensure uniform height
            rowTitle.setMinWidth(ROW_AND_COLUMN_SIZE);
            // Make row clickable to select
            final int finalRow = row;  // Use 'final' for the lambda
            rowTitle.setOnMouseClicked(e -> selectRow(finalRow));

            centerGrid.add(rowTitle, 0, row);
            GridPane.setHalignment(rowTitle, HPos.RIGHT);
            GridPane.setValignment(rowTitle, VPos.CENTER);
        }
    }

    private void initializeColumnsTitle() {
        for (int col = 1; col <= numOfColumns; col++) {
            Label columnTitle = new Label(getColumnLetter(col));
            columnTitle.getStyleClass().add("column-title");
            columnTitle.setMinWidth(columnWidthUnits);   // Match the cell width
            columnTitle.setMaxWidth(columnWidthUnits);   // Keep the label's size consistent
            columnTitle.setPrefWidth(columnWidthUnits);  // Ensure uniform width
            columnTitle.setMinHeight(ROW_AND_COLUMN_SIZE);

            // Make column clickable to select
            final int finalCol = col;  // Use 'final' for the lambda
            columnTitle.setOnMouseClicked(e -> selectColumn(finalCol));

            centerGrid.add(columnTitle, col, 0);
            GridPane.setHalignment(columnTitle, HPos.CENTER);
            GridPane.setValignment(columnTitle, VPos.CENTER);
        }
    }

    private void selectRow(int rowIndex) {
        clearSelection();

        // Loop through each column for the given row
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
        clearSelection();

        // Loop through each row for the given column
        for (int row = 1; row <= numOfRows; row++) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(row, colIndex);
            SingleCellController cellController = gridCells.get(coordinate);
            if (cellController != null) {
                cellController.getCellNode().getStyleClass().add("selected-column");
                selectedCells.add(cellController);
            }
        }
    }

    private void clearSelection() {
        for (SingleCellController cellController : selectedCells) {
            cellController.getCellNode().getStyleClass().removeAll("selected-row", "selected-column");
        }
        selectedCells.clear(); // Clear the list after removing styles
    }

    private String getColumnLetter(int columnNumber) {
        return String.valueOf((char) ('A' + columnNumber - 1));
    }

    private void setColumnsConstraints() {
        for (int col = 0; col <= numOfColumns; col++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(columnWidthUnits);
            colConstraints.setMaxWidth(columnWidthUnits);
            colConstraints.setPrefWidth(columnWidthUnits);
            centerGrid.getColumnConstraints().add(colConstraints);
        }
    }

    private void setRowsConstrains() {
        for (int row = 0; row <= numOfRows; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(rowHeightUnits);
            rowConstraints.setMaxHeight(rowHeightUnits);
            rowConstraints.setPrefHeight(rowHeightUnits);
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
