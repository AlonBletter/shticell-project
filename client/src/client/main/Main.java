package client.main;

import client.component.main.AppController;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    private AppController appMainController;

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL mainPage = getClass().getResource(Constants.MAIN_SCREEN_FXML_RESOURCE_LOCATION);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(mainPage);
        Parent root = fxmlLoader.load();

        appMainController = fxmlLoader.getController();
        appMainController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root, 350, 200);

        primaryStage.setTitle("Shticell Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        HttpClientUtil.shutdown();
        appMainController.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
