package client.component.dashboard;

import client.component.dashboard.load.LoadController;
import client.component.dashboard.permission.PermissionsController;
import client.component.dashboard.sheetlist.SheetListController;
import client.component.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML private BorderPane borderPane;
    @FXML private ScrollPane dashboardComponent;

    @FXML private VBox permissionsComponent;
    @FXML private PermissionsController permissionsComponentController;
    @FXML private VBox sheetListComponent;
    @FXML private SheetListController sheetListComponentController;
    @FXML private GridPane loadComponent;
    @FXML private LoadController loadComponentController;

    private AppController mainController;

    @FXML
    void initialize() {

    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        sheetListComponentController.setMainController(mainController);
        permissionsComponentController.setMainController(mainController);
        loadComponentController.setMainController(mainController);
    }

}
