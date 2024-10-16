package client.component.dashboard;

import client.component.dashboard.command.CommandsController;
import client.component.dashboard.load.LoadController;
import client.component.dashboard.permission.PermissionsController;
import client.component.dashboard.sheetlist.SheetListController;
import client.component.main.AppController;
import dto.SheetDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.Closeable;

public class DashboardController implements Closeable {

    @FXML private BorderPane borderPane;
    @FXML private ScrollPane dashboardComponent;

    @FXML private VBox permissionsComponent;
    @FXML private PermissionsController permissionsComponentController;
    @FXML private VBox sheetListComponent;
    @FXML private SheetListController sheetListComponentController;
    @FXML private GridPane loadComponent;
    @FXML private LoadController loadComponentController;
    @FXML private VBox commandsComponent;
    @FXML private CommandsController commandsComponentController;
    private AppController mainController;

    private SimpleStringProperty selectedSheetName = new SimpleStringProperty();

    @FXML
    void initialize() {
        if(sheetListComponentController != null && commandsComponentController != null) {
            sheetListComponentController.setDashboardController(this);
            commandsComponentController.setDashboardController(this);
        }
    }

    public SimpleStringProperty selectedSheetNameProperty() {
        return selectedSheetName;
    }

    public void setSelectedSheetName(String currentSelectedSheetName) {
        this.selectedSheetName.set(currentSelectedSheetName);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        sheetListComponentController.setMainController(mainController);
        permissionsComponentController.setMainController(mainController);
        loadComponentController.setMainController(mainController);
        // TODO commands
    }

    public void setActive() {
        sheetListComponentController.startListRefresher();
    }

    @Override
    public void close() {
        sheetListComponentController.close();
    }

    public void handleViewSheet(SheetDTO requestedSheet) {
        mainController.loadSheetView(requestedSheet);
    }
}
