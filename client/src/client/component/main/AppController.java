package client.component.main;

import client.component.dashboard.DashboardController;
import client.component.login.LoginController;
import client.component.sheet.app.SheetController;
import client.util.Constants;
import dto.SheetDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

public class AppController implements Closeable {
    @FXML StackPane mainPane;

    private GridPane loginComponent;
    private LoginController loginController;

    private ScrollPane dashboardComponent;
    private DashboardController dashboardController;

    private ScrollPane sheetComponent;
    private SheetController sheetController;

    private Stage primaryStage;
    private final Alert alert = new Alert(Alert.AlertType.ERROR);

    private final SimpleStringProperty username = new SimpleStringProperty();

    @FXML
    void initialize() {
        loadLoginScreen();
    }

    private void loadLoginScreen() {
        URL loginScreenUrl = getClass().getResource(Constants.LOGIN_SCREEN_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginScreenUrl);
            loginComponent = fxmlLoader.load();
            loginController = fxmlLoader.getController();
            loginController.setMainController(this);
            setMainPanelTo(loginComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDashboardScreen() {
        URL dashboardUrl = getClass().getResource(Constants.DASHBOARD_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(dashboardUrl);
            dashboardComponent = fxmlLoader.load();
            dashboardController = fxmlLoader.getController();
            dashboardController.setMainController(this);
            primaryStage.centerOnScreen();
            primaryStage.setWidth(dashboardComponent.getPrefWidth());
            primaryStage.setHeight(dashboardComponent.getPrefHeight());
            setMainPanelTo(dashboardComponent);
            dashboardController.setActive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSheetView(SheetDTO sheetToView, boolean readonly) {
        URL sheetUrl = getClass().getResource(Constants.SHEET_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(sheetUrl);
            sheetComponent = fxmlLoader.load();
            sheetController = fxmlLoader.getController();
            sheetController.setMainController(this);
            sheetController.setSheetToView(sheetToView, readonly);
            primaryStage.setWidth(sheetComponent.getPrefWidth());
            primaryStage.setHeight(sheetComponent.getPrefHeight());
            setMainPanelTo(sheetComponent);
            // sheetController.setActive(); //TODO timer for fetching new version available
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMainPanelTo(Parent paneToSet) {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(paneToSet);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void updateUsername(String username) {
        this.username.set(username);
    }

    public SimpleStringProperty usernameProperty() {
        return this.username;
    }

    public void showErrorAlert(String title, String headerText, String contentText) {
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @Override
    public void close() {
        if (dashboardController != null) {
            dashboardController.close();
        }
    }
}
