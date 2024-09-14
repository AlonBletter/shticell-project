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
import gui.singlecell.CellModel;
import gui.task.LoadFileTask;
import gui.task.LoadingDialogController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class AppController {
    private Engine engine = new EngineImpl();
    private CenterController centerComponentController;
    private Stage primaryStage;
    private Task<Boolean> currentRunningTask;

    public AppController() {
        this.centerComponentController = new CenterController();
    }

    @FXML private GridPane headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private BorderPane rootPane;

    @FXML
    public void initialize() {
        if (headerComponentController != null && centerComponentController != null) {
            headerComponentController.setMainController(this);
            centerComponentController.setMainController(this);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        if(headerComponentController != null) {
            headerComponentController.setPrimaryStage(primaryStage);
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
                rootPane.setCenter(centerComponentController.getCenterGrid());
                headerComponentController.enableButtonsAfterLoad();
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
            engine.updateCell(cellToUpdateCoordinate, newCellValue);
            List<CellDTO> lastModifiedCells = engine.getSpreadsheet().lastModifiedCells();
            centerComponentController.updateCells(lastModifiedCells);

            if(!lastModifiedCells.isEmpty()) {
                headerComponentController.refreshComboBoxVersion();
            }
        } catch (Exception e) {
            showErrorAlert("Updating Cell Error", "An error occurred while updating the cell.", e.getMessage());
        }
    }

    public void updateHeaderOnCellClick(CellModel selectedCell) {
        headerComponentController.updateHeaderCellData(selectedCell);
        headerComponentController.requestActionLineFocus();
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
}
