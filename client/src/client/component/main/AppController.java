package client.component.main;

import client.component.dashboard.DashboardController;
import client.component.login.LoginController;
import client.component.sheet.app.SheetController;
import client.util.Constants;
import dto.sheet.SheetDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

public class AppController implements Closeable {
    @FXML BorderPane mainPane;

    private ScrollPane loginComponent;
    private LoginController loginController;

    private ScrollPane dashboardComponent;
    private DashboardController dashboardController;

    private ScrollPane sheetComponent;
    private SheetController sheetController;

    private Stage primaryStage;

    private final SimpleStringProperty username = new SimpleStringProperty();
    private boolean loadedOnce = false;

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
            dashboardController.setStageDimension(primaryStage);
            if (!loadedOnce) {
                primaryStage.centerOnScreen();
                loadedOnce = true;
            }
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
            sheetController.setStageDimension(primaryStage);
            sheetController.setPrimaryStage(primaryStage);
            setMainPanelTo(sheetComponent);
            sheetController.setActive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMainPanelTo(Parent paneToSet) {
        mainPane.setCenter(paneToSet);
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

    @Override
    public void close() {
        if (dashboardController != null) {
            dashboardController.close();
        }

        if (sheetController != null) {
            sheetController.close();
        }
    }
}
