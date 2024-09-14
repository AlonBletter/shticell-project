package gui;

import gui.app.AppController;
import gui.common.ShticellResourcesConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class ShticellMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(ShticellResourcesConstants.APP_FXML_RESOURCE_IDENTIFIER);
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        AppController appController = fxmlLoader.getController();
        appController.setPrimaryStage(stage);

        Scene scene = new Scene(root, 1050, 600);
        stage.setScene(scene);
        stage.show();
    }
}
