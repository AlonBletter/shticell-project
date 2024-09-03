package gui.app;

import engine.Engine;
import engine.EngineImpl;
import gui.center.CellsGrid;
import gui.center.CenterController;
import gui.header.HeaderController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class AppController {
    private Engine engine = new EngineImpl();

    @FXML private GridPane headerComponent;
    @FXML private HeaderController headerComponentController;
//    @FXML private GridPane centerComponent;
//    @FXML private CenterController centerComponentController;
    @FXML private BorderPane rootPane;


    private CellsGrid centerComponent;
    private CenterController centerComponentController;

    @FXML
    public void initialize() {
        if (headerComponentController != null /*&& centerComponentController != null*/) {
            headerComponentController.setMainController(this);
        }
    }

    // Functionality
    public void loadFile(String filePath) {
        engine.loadSystemSettingsFromFile(filePath);
        centerComponent = new CellsGrid(engine.getSpreadsheet());
        rootPane.setCenter(centerComponent);
    }
}
