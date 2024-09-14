package gui.app;

import dto.CellDTO;
import dto.SheetDTO;
import engine.Engine;
import engine.EngineImpl;
import engine.sheet.coordinate.Coordinate;
import gui.center.CenterController;
import gui.header.HeaderController;
import gui.singlecell.CellModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class AppController {
    private Engine engine = new EngineImpl();
    private CenterController centerComponentController;
    private Stage primaryStage;

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
            engine.loadSystemSettingsFromFile(filePath);
            centerComponentController.initializeGrid(engine.getSpreadsheet());
            rootPane.setCenter(centerComponentController.getCenterGrid());
            headerComponentController.enableButtonsAfterLoad();
        } catch (Exception e) {
            showErrorAlert("File Loading Error", "An error occurred while loading the file.", e.getMessage());
        }
    }

    public void updateCell(Coordinate cellToUpdateCoordinate, String newCellValue) {
        try {
            engine.updateCell(cellToUpdateCoordinate, newCellValue);
            List<CellDTO> lastModifiedCells = engine.getSpreadsheet().lastModifiedCells();
            centerComponentController.updateCells(lastModifiedCells);

            if(!lastModifiedCells.isEmpty()) {
                headerComponentController.increaseComboBoxVersion();
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
}
