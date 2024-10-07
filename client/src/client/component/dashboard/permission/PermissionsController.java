package client.component.dashboard.permission;

import java.net.URL;
import java.util.ResourceBundle;

import client.component.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class PermissionsController {

    @FXML private VBox permissionsComponent;
    @FXML private Label permissionsListView;
    @FXML private ListView<?> sheetsListView;
    private AppController mainController;

    @FXML
    void initialize() {
        assert permissionsComponent != null : "fx:id=\"permissionsComponent\" was not injected: check your FXML file 'sheetList.fxml'.";
        assert permissionsListView != null : "fx:id=\"permissionsListView\" was not injected: check your FXML file 'sheetList.fxml'.";
        assert sheetsListView != null : "fx:id=\"sheetsListView\" was not injected: check your FXML file 'sheetList.fxml'.";

    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
