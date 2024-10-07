package client.component.main;

import client.component.dashboard.DashboardController;
import client.component.login.LoginController;
import client.util.Constants;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AppController {
    @FXML StackPane mainPane;

    private GridPane loginComponent;
    private LoginController loginController;

    private ScrollPane dashboardComponent;
    private DashboardController dashboardController;

    private Stage primaryStage;

    private SimpleStringProperty username = new SimpleStringProperty();

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
            setMainPanelTo(dashboardComponent);
            primaryStage.setWidth(dashboardComponent.getPrefWidth());
            primaryStage.setHeight(dashboardComponent.getPrefHeight());
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

    public void updateUsername(String username) {
        this.username.set(username);
    }

    public SimpleStringProperty usernameProperty() {
        return this.username;
    }
}
