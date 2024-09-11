package gui.app;

import engine.Engine;
import engine.EngineImpl;
import engine.sheet.coordinate.Coordinate;
import gui.center.CenterController;
import gui.header.HeaderController;
import gui.singlecell.CellModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class AppController {
    private Engine engine = new EngineImpl();
    private CenterController centerComponentController;

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

    // Functionality
    public void loadFile(String filePath) {
        try {
            engine.loadSystemSettingsFromFile(filePath);
            centerComponentController.initializeGrid(engine.getSpreadsheet());
            rootPane.setCenter(centerComponentController.getCenterGrid());
        } catch (Exception e) {
            showErrorAlert("File Loading Error", "An error occurred while loading the file.", e.getMessage());
        }
    }

    public void updateCell(Coordinate cellToUpdateCoordinate, String newCellValue) {
        try {
            engine.updateCell(cellToUpdateCoordinate, newCellValue);
            centerComponentController.updateCells(engine.getSpreadsheet().lastModifiedCells());
        } catch (Exception e) {
            showErrorAlert("Updating Cell Error", "An error occurred while updating the cell.", e.getMessage());
        }
    }

    public void updateHeaderOnCellClick(CellModel selectedCell) {
        headerComponentController.updateHeaderCellData(selectedCell);
        headerComponentController.requestActionLineFocus();
    }



    private void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }
}
