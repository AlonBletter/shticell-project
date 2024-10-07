package client.component.dashboard.load;

import java.net.URL;
import java.util.ResourceBundle;

import client.component.main.AppController;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class LoadController {

    @FXML private GridPane loadComponent;
    @FXML private Button loadFileButton;
    @FXML private Label loadedFilePathLabel;
    @FXML private Label usernameLabel;

    private AppController mainController;

    @FXML
    void initialize() {

    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {

    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        usernameLabel.textProperty().bind(Bindings.concat("Logged As ", mainController.usernameProperty()));
    }
}
