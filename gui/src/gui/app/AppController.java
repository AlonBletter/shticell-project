package gui.app;

import engine.Engine;
import engine.EngineImpl;
import engine.sheet.coordinate.Coordinate;
import gui.center.CenterController;
import gui.header.HeaderController;
import gui.singlecell.CellModel;
import gui.singlecell.SingleCellController;
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

    private void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public void updateHeaderOnCellClick(CellModel currentSelectedCell) {
        headerComponentController.update
    }
}
