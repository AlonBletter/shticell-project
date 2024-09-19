package gui.app;

import dto.CellDTO;
import dto.SheetDTO;
import engine.Engine;
import engine.EngineImpl;
import engine.exception.InvalidCellBoundsException;
import engine.sheet.coordinate.Coordinate;
import gui.common.ShticellResourcesConstants;
import gui.center.CenterController;
import gui.header.HeaderController;
import gui.left.LeftController;
import gui.singlecell.SingleCellController;
import gui.task.LoadFileTask;
import gui.task.LoadingDialogController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class AppController {
    @FXML private GridPane headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private VBox leftComponent;
    @FXML private LeftController leftComponentController;
    @FXML private BorderPane borderPane;
    @FXML private ScrollPane scrollPane;

    private Engine engine;
    private CenterController centerComponentController;
    private Stage primaryStage;
    private Task<Boolean> currentRunningTask;
    private SingleCellController selectedCell;
    private List<SingleCellController> selectedRow;
    private List<SingleCellController> selectedColumn;
    private SimpleBooleanProperty isFileLoaded;

    public AppController() {
        engine = new EngineImpl();
        this.centerComponentController = new CenterController();
        this.isFileLoaded = new SimpleBooleanProperty(false);
    }

    public SimpleBooleanProperty isFileLoadedProperty() {
        return isFileLoaded;
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
        updateHeaderOnCellClick(); //TODO CHANGE TO UPDATE UI?
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
        return centerComponentController.getRowHeight(selectedCell.getCoordinate().getRow());
    }

    public double getCurrentSelectedColumnWidth() {
        return centerComponentController.getColumnWidth(selectedCell.getCoordinate().getColumn());
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

    public void loadFile(String filePath) {
        try {
            FXMLLoader loader = new FXMLLoader(ShticellResourcesConstants.LOADING_DIALOG_URL);
            Parent root = loader.load();
            LoadingDialogController loadingDialogController = loader.getController();

            Consumer<Void> onSuccess = getLoadFileConsumerSuccess(filePath);
            Consumer<Exception> onFailure = getLoadFileConsumerFailure();
            currentRunningTask = new LoadFileTask(filePath, onSuccess, onFailure);
            loadingDialogController.bindTaskToUIComponents(currentRunningTask, null);
            loadingDialogController.setLoadingFileTitleLabel(filePath);

            Stage loadingDialogStage = new Stage();
            loadingDialogStage.initModality(Modality.APPLICATION_MODAL);
            loadingDialogStage.setTitle("Loading file");
            loadingDialogStage.setResizable(false);
            loadingDialogStage.setScene(new Scene(root));
            loadingDialogStage.show();

            new Thread(currentRunningTask).start();
        } catch (IOException e) {
            showErrorAlert("File Loading Error", "An error occurred while opening the file dialog.", e.getMessage());
        }
    }
    //TODO is this good?

    private Consumer<Exception> getLoadFileConsumerFailure() {
        return (exception) -> {
            showErrorAlert("File Loading Error", "An error occurred while loading the file.", exception.getMessage());
            currentRunningTask.cancel();
        };
    }

    private Consumer<Void> getLoadFileConsumerSuccess(String filePath) {
        return (v) -> {
            try {
                engine.loadSystemSettingsFromFile(filePath);
                centerComponentController.initializeGrid(engine.getSpreadsheet());
                borderPane.setCenter(centerComponentController.getCenterGrid());
                headerComponentController.enableButtonsAfterLoad(); // TODO not enables anymore, only refresh combo box
                isFileLoaded.set(true);
            } catch (InvalidCellBoundsException e) {
                handleInvalidCellBoundException(e);
            } catch (Exception e) { //TODO think about closing both windows when error occurs.
                showErrorAlert("File Loading Error", "An error occurred while processing the loaded file.", e.getMessage());
            } finally {
                currentRunningTask.cancel();
            }
        };
    }

    public void updateCell(Coordinate cellToUpdateCoordinate, String newCellValue) {
        try {
            int oldVersion = engine.getCurrentVersionNumber();
            engine.updateCell(cellToUpdateCoordinate, newCellValue);
            //List<CellDTO> lastModifiedCells = engine.getSpreadsheet().lastModifiedCells();
            //centerComponentController.updateCells(lastModifiedCells);

            if(oldVersion != engine.getCurrentVersionNumber()) {
                centerComponentController.updateCells(engine.getSpreadsheet());
                headerComponentController.refreshComboBoxVersion();
            }
        } catch (Exception e) {
            showErrorAlert("Updating Cell Error", "An error occurred while updating the cell.", e.getMessage());
        }
    }

    public int getSheetCurrentVersion() {
        return engine.getCurrentVersionNumber();
    }

    public void displaySheetByVersion(int version) {
        try {
            SheetDTO sheetVersion = engine.getSheetByVersion(version);
            CenterController centerController = new CenterController();
            centerController.initializeGrid(sheetVersion);

            Stage sheetStage = new Stage();
            sheetStage.setTitle("Sheet Version: " + version);
            sheetStage.initModality(Modality.APPLICATION_MODAL);
            GridPane gridPane = centerController.getCenterGrid();
            Scene scene = new Scene(gridPane);

            sheetStage.setScene(scene);
            sheetStage.showAndWait();
        } catch (Exception e) {
            showErrorAlert("Invalid Version", "An error occurred while displaying the sheet.", e.getMessage());
        }
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
        char cellColumnChar = (char) (coordinate.getColumn() + 'A' - 1);

        String message = e.getMessage() != null ? e.getMessage() : "";

        showErrorAlert("Invalid cells bounds", "An error occurred while processing the loaded file..",
                message + "Expected column between A-" + sheetColumnRange + " and row between 1-" + sheetNumOfRows + "\n" +
                "But received column [" + cellColumnChar + "] and row [" + coordinate.getRow() + "]");
    }

    public void updateColumnWidth(Double result) {
        int columnIndex;

        if(selectedCell != null) {
            columnIndex = selectedCell.getCoordinate().getColumn();
        } else {
            columnIndex = selectedColumn.getFirst().getCoordinate().getColumn();
        }

        centerComponentController.updateColumnWidth(columnIndex, result);
    }

    public void updateRowHeight(Double result) {
        int rowIndex;

        if(selectedCell != null) {
            rowIndex = selectedCell.getCoordinate().getRow();
        } else {
            rowIndex = selectedRow.getFirst().getCoordinate().getRow();
        }

        centerComponentController.updateRowHeight(rowIndex, result);
    }

    public void alignColumnCells(Pos pos) {
        centerComponentController.alignColumnCells(selectedCell.getCoordinate().getColumn(), pos);
    }

    public void updateCellBackgroundColor(String newColor) {
        Coordinate currentCellCoordinate = selectedCell.getCoordinate();

        engine.updateCellBackgroundColor(currentCellCoordinate, newColor);
        centerComponentController.updateCellBackgroundColor(currentCellCoordinate, newColor);
    }

    public void updateCellTextColor(String newColor) {
        Coordinate currentCellCoordinate = selectedCell.getCoordinate();

        engine.updateCellTextColor(currentCellCoordinate, newColor);
        centerComponentController.updateCellTextColor(currentCellCoordinate, newColor);
    }
}
