package client.component.sheet.app;

import client.component.main.AppController;
import client.component.sheet.center.CenterController;
import client.component.sheet.center.singlecell.SingleCellController;
import client.component.sheet.header.HeaderController;
import client.component.sheet.left.LeftController;
import client.util.http.SheetService;
import client.util.http.SheetServiceImpl;
import dto.CellDTO;
import dto.SheetDTO;
import engine.exception.InvalidCellBoundsException;
import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SheetController implements Closeable {
    @FXML private GridPane headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private VBox leftComponent;
    @FXML private LeftController leftComponentController;
    @FXML private BorderPane borderPane;
    @FXML private ScrollPane mainScrollPane;

    private final SheetService sheetService;
    private CenterController centerComponentController;
    private ScrollPane centerScrollPane;
    private Stage primaryStage;
    private SingleCellController selectedCell;
    private List<SingleCellController> selectedRow;
    private List<SingleCellController> selectedColumn;
    private final SimpleBooleanProperty readonlyPresentation;
    private final SimpleBooleanProperty textSpinAnimation;
    private final SimpleBooleanProperty textFadeAnimation;
    private AppController mainController;

    public SheetController() {
        sheetService = new SheetServiceImpl();
        centerScrollPane = new ScrollPane();
        centerComponentController = new CenterController();
        this.readonlyPresentation = new SimpleBooleanProperty(false); //TODO change to permissions
        this.textSpinAnimation = new SimpleBooleanProperty(false);
        this.textFadeAnimation = new SimpleBooleanProperty(false);
    }

    @Override
    public void close() throws IOException {

    }

    public SimpleBooleanProperty readonlyPresentationProperty() {
        return readonlyPresentation;
    }

    public SimpleBooleanProperty getTextSpinAnimationProperty() {
        return textSpinAnimation;
    }

    public SimpleBooleanProperty textFadeAnimationProperty() {
        return textFadeAnimation;
    }

    @FXML
    public void initialize() {
        if (headerComponentController != null && centerComponentController != null && leftComponentController != null) {
            headerComponentController.setMainController(this);
            centerComponentController.setMainController(this);
            leftComponentController.setMainController(this);
        }
    }

    public void setSelectedCell(SingleCellController selectedCell) {
        clearSelections();
        this.selectedCell = selectedCell;
        updateHeaderOnCellClick();
        leftComponentController.updateView(selectedCell.getAlignment());
    }

    public void setSelectedRow(List<SingleCellController> selectedRow) {
        clearSelections();
        this.selectedRow = selectedRow;
    }

    public void setSelectedColumn(List<SingleCellController> selectedColumn) {
        clearSelections();
        this.selectedColumn = selectedColumn;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        if(headerComponentController != null && leftComponentController != null) {
            headerComponentController.setPrimaryStage(primaryStage);
            leftComponentController.setPrimaryStage(primaryStage);
        }
    }

    public double getCurrentSelectedRowHeight() {
        return centerComponentController.getRowHeight(selectedCell.getCoordinate().row());
    }

    public double getCurrentSelectedColumnWidth() {
        return centerComponentController.getColumnWidth(selectedCell.getCoordinate().column());
    }

    public void updateHeaderOnCellClick() {
        headerComponentController.updateHeaderCellData(selectedCell);
        headerComponentController.requestActionLineFocus();
    }

    private void clearSelections() {
        centerComponentController.clearSelection();

        if(selectedCell != null) {
            selectedCell.clearSelection();
        }
    }

    public void setSheetToView(SheetDTO sheet, boolean readonly) {
        try {
            centerComponentController.initializeGrid(sheet, true);
            centerScrollPane.setContent(centerComponentController.getCenterGrid());
//                centerScrollPane.setFitToWidth(true);
//                centerScrollPane.setFitToHeight(true);
            borderPane.setCenter(centerScrollPane);
            headerComponentController.initializeHeaderAfterLoad();
            //leftComponentController.loadRanges(sheetService.getRanges()); //TODO fix
            readonlyPresentation.set(readonly);
        } catch (Exception e) {
            showErrorAlert("File Loading Error", "An error occurred while processing the loaded file.", e.getMessage());
        }
    }

    public boolean updateCell(Coordinate cellToUpdateCoordinate, String newCellValue) {
        try {
            int oldVersion = sheetService.getCurrentVersionNumber();
            sheetService.updateCell(cellToUpdateCoordinate, newCellValue);

            if(oldVersion != sheetService.getCurrentVersionNumber()) {
                centerComponentController.updateCells(sheetService.getSpreadsheet());
                headerComponentController.refreshComboBoxVersion();
            }
            return true;
        } catch (Exception e) {
            showErrorAlert("Updating Cell Error", "An error occurred while updating the cell.", e.getMessage());
            return false;
        }
    }

    public void displaySheetByVersion(int version) {
        try {
            SheetDTO sheetVersion = sheetService.getSheetByVersion(version);
            String titleToDisplay = "Sheet Version: " + version;
            displaySheet(sheetVersion, titleToDisplay);
        } catch (Exception e) {
            showErrorAlert("Invalid Version", "An error occurred while displaying the sheet.", e.getMessage());
        }
    }

    private void displaySheet(SheetDTO sheetToDisplay, String title) {
        CenterController centerController = new CenterController();
        centerController.initializeGrid(sheetToDisplay, false);

        ScrollPane displayScrollPane = new ScrollPane();
        displayScrollPane.setContent(centerController.getCenterGrid());
        Stage sheetStage = new Stage();
        sheetStage.setTitle(title);
        sheetStage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(displayScrollPane);
        sheetStage.setWidth(800);
        sheetStage.setHeight(600);
        sheetStage.setScene(scene);
        sheetStage.showAndWait();
    }

    private void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void handleInvalidCellBoundException(InvalidCellBoundsException e) {
        Coordinate coordinate = e.getActualCoordinate();
        int sheetNumOfRows = e.getSheetNumOfRows();
        int SheetNumOfColumns = e.getSheetNumOfColumns();
        char sheetColumnRange = (char) (SheetNumOfColumns + 'A' - 1);
        char cellColumnChar = (char) (coordinate.column() + 'A' - 1);
        String message = e.getMessage() != null ? e.getMessage() : "";

        showErrorAlert("Invalid cells bounds", "An error occurred while processing the loaded file..",
                message + "\n" + "Expected column between A-" + sheetColumnRange + " and row between 1-" + sheetNumOfRows + "\n" +
                "But received column [" + cellColumnChar + "] and row [" + coordinate.row() + "].");
    }

    public void updateColumnWidth(Double result) {
        int columnIndex;

        if(selectedCell != null) {
            columnIndex = selectedCell.getCoordinate().column();
        } else {
            columnIndex = selectedColumn.getFirst().getCoordinate().column();
        }

        centerComponentController.updateColumnWidth(columnIndex, result);
    }

    public void updateRowHeight(Double result) {
        int rowIndex;

        if(selectedCell != null) {
            rowIndex = selectedCell.getCoordinate().row();
        } else {
            rowIndex = selectedRow.getFirst().getCoordinate().row();
        }

        centerComponentController.updateRowHeight(rowIndex, result);
    }

    public void alignColumnCells(Pos pos) {
        centerComponentController.alignColumnCells(selectedCell.getCoordinate().column(), pos);
    }

    public void updateCellBackgroundColor(String newColor) {
        Coordinate currentCellCoordinate = selectedCell.getCoordinate();

        sheetService.updateCellBackgroundColor(currentCellCoordinate, newColor);
        centerComponentController.updateCellBackgroundColor(currentCellCoordinate, newColor);
    }

    public void updateCellTextColor(String newColor) {
        Coordinate currentCellCoordinate = selectedCell.getCoordinate();

        sheetService.updateCellTextColor(currentCellCoordinate, newColor);
        centerComponentController.updateCellTextColor(currentCellCoordinate, newColor);
    }

    public boolean addRange(String rangeName, String rangeCoordinates) {
        try {
            sheetService.addRange(rangeName, rangeCoordinates);
            return true;
        } catch (InvalidCellBoundsException e) {
            handleInvalidCellBoundException(e);
            return false;
        } catch (Exception e) {
            showErrorAlert("Invalid Range Addition", "An error occurred while adding the range.", e.getMessage());
            return false;
        }
    }

    public boolean deleteRange(String rangeNameToDelete) {
        try {
            sheetService.deleteRange(rangeNameToDelete);
            centerComponentController.unmarkRange();
            return true;
        } catch (Exception e) {
            showErrorAlert("Invalid Range Deletion", "An error occurred while deleting the range.", e.getMessage());
            return false;
        }
    }

    public void viewRange(String rangeNameToView) {
        List<Coordinate> cellsInRange = sheetService.getRange(rangeNameToView);
        centerComponentController.markRange(cellsInRange);
    }

    public void sortRange(String rangeCoordinatesToSort, List<String> columnsToSortBy) {
        SheetDTO sortedSheet;

        try {
            sortedSheet = sheetService.getSortedSheet(rangeCoordinatesToSort, columnsToSortBy);
        } catch (InvalidCellBoundsException e) {
            handleInvalidCellBoundException(e);
            return;
        } catch (Exception e) {
            showErrorAlert("Invalid Sort Request", "An error occurred while sorting the range.", e.getMessage());
            return;
        }

        displaySheet(sortedSheet, "Sorted Sheet");
    }

    public int getNumberOfColumns() {
        //add method in sheetService api that gets the number of column (consider not mandatory)
        return sheetService.getSpreadsheet().numOfColumns();
    }

    public int getSheetCurrentVersion() {
        return sheetService.getCurrentVersionNumber();
    }

    public List<String> getColumnUniqueValues(String columnLetter) {
        return sheetService.getColumnUniqueValue(columnLetter);
    }

    public void filterRange(String rangeToFilter, Map<String, List<String>> filterRequestValues) {
        SheetDTO filteredSheet;
        try {
            filteredSheet = sheetService.getFilteredSheet(rangeToFilter, filterRequestValues);
        } catch (InvalidCellBoundsException e) {
            handleInvalidCellBoundException(e);
            return;
        } catch (Exception e) {
            showErrorAlert("Invalid Filter Request", "An error occurred while filtering the range.", e.getMessage());
            return;
        }

        displaySheet(filteredSheet, "Filtered Sheet");
    }

    public void setComponentsSkin(String newSkin) {
        headerComponentController.setSkin(newSkin);
        leftComponentController.setSkin(newSkin);
        centerComponentController.setSkin(newSkin);
    }

    public void setAnimations(String newValue) {
        textSpinAnimation.set(false);
        textFadeAnimation.set(false);

        switch (newValue) {
            case "Dancing Cells":
                textSpinAnimation.set(true);
                break;
            case "New Value Fade":
                textFadeAnimation.set(true);
                break;
        }
    }

    public void displayExpectedValue(Number newValue) {
        try {
//            Map<Coordinate, EffectiveValue> modifiedCells = sheetService.getExpectedValue(selectedCell.getCoordinate(), String.valueOf(newValue.doubleValue()));
            SheetDTO sheet = sheetService.getExpectedValue(selectedCell.getCoordinate(), String.valueOf(newValue.doubleValue()));
            centerComponentController.refreshExpectedValues(sheet);
        } catch (Exception e) {
            showErrorAlert("Invalid Usage of What-If",
                    "An error occurred while using the what-if command.", e.getMessage());
        }
    }

    public void restoreCurrentValues() {
        centerComponentController.restoreCurrentValues();
    }

    public CellDTO getCurrentCell() {
        return sheetService.getCell(selectedCell.getCoordinate());
    }

    public void createGraph(String xAxisRange, String yAxisRange, String chartType) {
        List<Coordinate> xAxis = null;
        List<Coordinate> yAxis = null;

        try {
            xAxis = sheetService.getAxis(xAxisRange);
            yAxis = sheetService.getAxis(yAxisRange);
        } catch (Exception e) {
            showErrorAlert("Invalid graph settings", "An error occurred while creating the graph.",
                    "Invalid column range format. Expected <coordinate first column cell>..<coordinate last column cell>");
            return;
        }

        if (xAxis.getFirst().column() != xAxis.getLast().column()
                || yAxis.getFirst().column() != yAxis.getLast().column()) {

            showErrorAlert("Invalid graph settings",
                    "An error occurred while creating the graph.", "Invalid Column");
            return;
        }

        List<Double> xAxisData = getValuesFromCoordinates(xAxis);
        List<Double> yAxisData = getValuesFromCoordinates(yAxis);

        if (xAxisData.size() != yAxisData.size()) {
            showErrorAlert("Mismatch in data size",
                    "X-axis and Y-axis ranges must have the same number of data points.", null);
            return;
        }

        switch (chartType) {
            case "BarChart":
                showBarChart(xAxisData, yAxisData);
                break;
            case "LineChart":
                showLineChart(xAxisData, yAxisData);
                break;
            default:
                showErrorAlert("Invalid chart type",
                        "Unknown chart type: " + chartType, null);
                break;
        }
    }

    private List<Double> getValuesFromCoordinates(List<Coordinate> coordinates) {
        List<Double> values = new ArrayList<>();
        for (Coordinate coord : coordinates) {
            CellDTO cell = sheetService.getCell(coord);
            EffectiveValue value = cell.effectiveValue();
            if (value.cellType() == CellType.NUMERIC) {
                values.add(value.extractValueWithExpectation(Double.class));
            } else {
                showErrorAlert("Non-numeric data", "All cells in the graph must be numeric.", null);
                return null;
            }
        }
        return values;
    }

    private void showBarChart(List<Double> xAxisData, List<Double> yAxisData) {
        Stage stage = new Stage();

        List<String> newAxis = xAxisData.stream()
                .map(Object::toString)
                .toList();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X Axis");
        yAxis.setLabel("Y Axis");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Bar Chart");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            series.getData().add(new XYChart.Data<>(newAxis.get(i), yAxisData.get(i)));
        }
        barChart.getData().add(series);

        VBox vbox = new VBox(barChart);
        Scene scene = new Scene(vbox, 800, 600);

        stage.setScene(scene);
        stage.show();
    }

    private void showLineChart(List<Double> xAxisData, List<Double> yAxisData) {
        Stage stage = new Stage();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X Axis");
        yAxis.setLabel("Y Axis");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Line Chart");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            series.getData().add(new XYChart.Data<>(xAxisData.get(i), yAxisData.get(i)));
        }
        lineChart.getData().add(series);

        VBox vbox = new VBox(lineChart);
        Scene scene = new Scene(vbox, 800, 600);

        stage.setScene(scene);
        stage.show();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
