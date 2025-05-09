package client.component.dashboard;

import client.component.dashboard.command.CommandsController;
import client.component.dashboard.load.LoadController;
import client.component.dashboard.permission.PermissionsController;
import client.component.dashboard.sheetlist.SheetListController;
import client.component.main.AppController;
import dto.sheet.SheetDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    private final SimpleStringProperty selectedSheetName = new SimpleStringProperty();
    private final SimpleBooleanProperty isSelectedSheet = new SimpleBooleanProperty();
    private final SimpleBooleanProperty isUserOwnerOfSelectedSheet = new SimpleBooleanProperty();
    private final SimpleBooleanProperty isUserHasNoPermissions = new SimpleBooleanProperty();
    private final SimpleBooleanProperty isPermissionRequestSelected = new SimpleBooleanProperty();
    private final SimpleIntegerProperty requestID = new SimpleIntegerProperty();
    private final SimpleBooleanProperty isUserHasReaderPermission = new SimpleBooleanProperty();

    @FXML
    void initialize() {
        if(sheetListComponentController != null && commandsComponentController != null && permissionsComponentController != null && loadComponentController != null) {
            sheetListComponentController.setDashboardController(this);
            commandsComponentController.setDashboardController(this);
            permissionsComponentController.setDashboardController(this);
            loadComponentController.setDashboardController(this);
        }
    }

    public SimpleBooleanProperty isUserHasReaderPermissionProperty() {
        return isUserHasReaderPermission;
    }

    public void setIsUserHasReaderPermission(boolean isUserHasReaderPermission) {
        this.isUserHasReaderPermission.set(isUserHasReaderPermission);
    }

    public SimpleIntegerProperty requestIDProperty() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID.set(requestID);
    }

    public SimpleBooleanProperty isPermissionRequestSelectedProperty() {
        return isPermissionRequestSelected;
    }

    public void setIsPermissionRequestSelected(boolean isPermissionRequestSelected) {
        this.isPermissionRequestSelected.set(isPermissionRequestSelected);
    }

    public SimpleBooleanProperty isUserHasNoPermissionsProperty() {
        return isUserHasNoPermissions;
    }

    public void setIsUserHasNoPermissions(boolean isUserHasNoPermissions) {
        this.isUserHasNoPermissions.set(isUserHasNoPermissions);
    }

    public SimpleBooleanProperty isUserOwnerOfSelectedSheetProperty() {
        return isUserOwnerOfSelectedSheet;
    }

    public void setIsUserOwnerOfSelectedSheet(boolean isUserOwnerOfSelectedSheet) {
        this.isUserOwnerOfSelectedSheet.set(isUserOwnerOfSelectedSheet);
    }

    public SimpleBooleanProperty isSelectedSheetProperty() {
        return isSelectedSheet;
    }

    public SimpleStringProperty selectedSheetNameProperty() {
        return selectedSheetName;
    }

    public void setSelectedSheetName(String currentSelectedSheetName) {
        this.selectedSheetName.set(currentSelectedSheetName);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;

        loadComponentController.updateUsernameLabel("Logged As: " + mainController.usernameProperty().get());
    }

    public void setActive() {
        sheetListComponentController.startListRefresher();
    }

    @Override
    public void close() {
        sheetListComponentController.close();
        commandsComponentController.close();
    }

    public void handleViewSheet(SheetDTO requestedSheet) {
        close();
        mainController.loadSheetView(requestedSheet, isUserHasReaderPermission.getValue());
    }

    public Stage getPrimaryStage() {
        return mainController.getPrimaryStage();
    }

    public void refreshPermissions() {
        permissionsComponentController.refreshView();
    }

    public void setStageDimension(Stage stage) {
        stage.setWidth(borderPane.getPrefWidth() + 50);
        stage.setHeight(borderPane.getPrefHeight() + 50);
    }

    public SimpleStringProperty usernameProperty() {
        return mainController.usernameProperty();
    }
}
