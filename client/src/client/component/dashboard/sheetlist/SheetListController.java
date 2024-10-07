package client.component.dashboard.sheetlist;

import client.component.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class SheetListController {

    @FXML private ListView<?> sheetsListView;
    private AppController mainController;

    @FXML
    void initialize() {
        assert sheetsListView != null : "fx:id=\"sheetsListView\" was not injected: check your FXML file 'sheetList.fxml'.";

    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
