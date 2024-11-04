package client.component.sheet.app;

import client.component.main.AppController;
import client.component.sheet.center.CenterController;
import client.component.sheet.center.singlecell.CellModel;
import client.component.sheet.center.singlecell.SingleCellController;
import client.component.sheet.header.HeaderController;
import client.component.sheet.left.LeftController;
import client.util.sheetservice.SheetService;
import client.util.sheetservice.SheetServiceImpl;
import dto.coordinate.CoordinateFactory;
import dto.sheet.SheetDTO;
import dto.requestinfo.UpdateInformation;
import dto.cell.CellType;
import dto.coordinate.Coordinate;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SheetController implements Closeable {
    @FXML private GridPane headerComponent;
    @FXML private HeaderController headerComponentController;

    @FXML private VBox leftComponent;
    @FXML private LeftController leftComponentController;

    private ScrollPane centerScrollPane;
    private CenterController centerComponentController;

    @FXML private BorderPane borderPane;
    @FXML private ScrollPane mainScrollPane;

    @FXML private Button backButton;

    private Stage primaryStage;
    private AppController mainController;

    private final SheetService sheetService;
    private SingleCellController selectedCell;
    private List<SingleCellController> selectedRow;
    private List<SingleCellController> selectedColumn;
    private final SimpleBooleanProperty readonlyPresentation;
    private final SimpleBooleanProperty textSpinAnimation;
    private final SimpleBooleanProperty textFadeAnimation;
    private final SimpleIntegerProperty version;

    public SheetController() {
        sheetService = new SheetServiceImpl();
        centerScrollPane = new ScrollPane();
        centerComponentController = new CenterController();
        readonlyPresentation = new SimpleBooleanProperty(false);
        textSpinAnimation = new SimpleBooleanProperty(false);
        textFadeAnimation = new SimpleBooleanProperty(false);
        version = new SimpleIntegerProperty(1);
    }

    public int getVersion() {
        return version.get();
    }

    public SimpleIntegerProperty versionProperty() {
        return version;
    }

    public void setVersion(int version) {
        this.version.set(version);
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
        centerComponentController.initializeGrid(sheet, true);
        centerScrollPane.setContent(centerComponentController.getCenterGrid());
        borderPane.setCenter(centerScrollPane);
        setVersion(sheet.versionNumber());
        headerComponentController.initializeHeaderAfterLoad(mainController.usernameProperty().get());
        leftComponentController.loadRanges(sheet.ranges());
        readonlyPresentation.set(readonly);
    }

    public void updateCell(Coordinate cellToUpdateCoordinate, String newCellValue) {
        UpdateInformation updateInformation = new UpdateInformation(cellToUpdateCoordinate, newCellValue, getVersion());

        sheetService.updateCell(updateInformation, (newSheet) -> {
            setVersion(version.get() + 1);
            centerComponentController.updateCells(newSheet);
            headerComponentController.updateHeader();
        });
    }

    public void displaySheetByVersion(int version) {
        sheetService.getSheetByVersion(version, (sheetVersion) -> {
            String titleToDisplay = "Sheet Version: " + version;
            displaySheet(sheetVersion, titleToDisplay);
        });
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
        UpdateInformation updateInformation = new UpdateInformation(currentCellCoordinate, newColor, getVersion());

        sheetService.updateCellBackgroundColor(updateInformation, () ->
                centerComponentController.updateCellBackgroundColor(currentCellCoordinate, newColor)
        );
    }

    public void updateCellTextColor(String newColor) {
        Coordinate currentCellCoordinate = selectedCell.getCoordinate();
        UpdateInformation updateInformation = new UpdateInformation(currentCellCoordinate, newColor, getVersion());

        sheetService.updateCellTextColor(updateInformation, () ->
                centerComponentController.updateCellTextColor(currentCellCoordinate, newColor)
        );
    }

    public void addRange(String rangeName, String rangeCoordinates) {
        sheetService.addRange(rangeName, rangeCoordinates, getVersion(), (newRange) ->
                leftComponentController.updateRanges(newRange));
    }

    public void deleteRange(String rangeNameToDelete) {
        sheetService.deleteRange(rangeNameToDelete, getVersion(), () -> {
                    centerComponentController.unmarkRange();
                    leftComponentController.deleteRange(rangeNameToDelete);
        });
    }

    public void viewRange(List<Coordinate> rangeCoordinatesToMark) {
        centerComponentController.markRange(rangeCoordinatesToMark);
    }

    public void sortRange(String rangeCoordinatesToSort, List<String> columnsToSortBy) {
        sheetService.getSortedSheet(rangeCoordinatesToSort, columnsToSortBy, (sortedSheet) ->
                displaySheet(sortedSheet, "Sorted Sheet")
        );
    }

    public int getNumberOfColumns() {
        return centerComponentController.getNumOfColumns();
    }

    public int getNumberOfRows() {
        return centerComponentController.getNumOfRows();
    }

    public List<String> getColumnUniqueValues(String columnLetter) {
        return centerComponentController.getColumnUniqueValue(columnLetter);
    }

    public void filterRange(String rangeToFilter, Map<String, List<String>> filterRequestValues) {
        sheetService.getFilteredSheet(rangeToFilter, filterRequestValues, (filteredSheet) ->
                displaySheet(filteredSheet, "Filtered Sheet"));
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

    public void displayExpectedValue(Number newValue , Coordinate cellId) {
        UpdateInformation updateInformation = new UpdateInformation(
                cellId, String.valueOf(newValue.doubleValue()), getVersion());

        sheetService.getExpectedValue(updateInformation, (expectedSheet) ->
                centerComponentController.refreshExpectedValues(expectedSheet));
    }

    public void restoreCurrentValues() {
        centerComponentController.restoreCurrentValues();
    }

    public SingleCellController getSelectedCell() {
        return selectedCell;
    }

    public void createGraph(String xAxisRange, String yAxisRange, String chartType) {
        sheetService.getAxis(xAxisRange, xAxis -> {
            if (xAxis.getFirst().column() != xAxis.getLast().column()) {
                showErrorAlert("Invalid graph settings",
                        "An error occurred while creating the graph.", "Invalid Column");
                return;
            }

            sheetService.getAxis(yAxisRange, yAxis -> {
                if (yAxis.getFirst().column() != yAxis.getLast().column()) {
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
            });
        });
    }

    private List<Double> getValuesFromCoordinates(List<Coordinate> coordinates) {
        List<Double> values = new ArrayList<>();
        for (Coordinate coord : coordinates) {
            CellModel cell = centerComponentController.getCell(coord);
            String value = cell.getEffectiveValue();
            if (cell.getCellType() == CellType.NUMERIC) {
                values.add(Double.parseDouble(value));
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

    public void setActive() {
        headerComponentController.startVersionRefresher();
    }

    @Override
    public void close() {
        headerComponentController.close();
    }

    @FXML
    public void backButtonOnClicked(ActionEvent event) {
        close();
        mainController.loadDashboardScreen();
    }

    public void setStageDimension(Stage primaryStage) {
        primaryStage.setWidth(borderPane.getPrefWidth() + 50);
        primaryStage.setMinHeight(borderPane.getPrefHeight() + 50);
    }
}
