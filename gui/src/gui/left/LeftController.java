package gui.left;

import gui.app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LeftController {

    @FXML private Button columnWidthButton;
    @FXML private Button rowHeightButton;
    private AppController mainController;

    @FXML
    private void initialize() {

    }

    @FXML
    void RowHeightButtonAction(ActionEvent event) {

    }

    @FXML
    void columnWidthButtonAction(ActionEvent event) {

    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

}
