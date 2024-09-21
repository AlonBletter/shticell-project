package gui.left.rangedialog;

import gui.app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class RangeDialogController {
    @FXML private Button addButton;
    @FXML private Button cancelButton;
    @FXML private TextField rangeCoordinatesTextField;
    @FXML private TextField rangeNameTextField;

    private Stage dialogStage;
    private AppController mainController;
    private boolean confirmed = false;

    @FXML
    void initialize() {
        rangeNameTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addButton.fire();
            }
        });

        rangeCoordinatesTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addButton.fire();
            }
        });
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getRangeCoordinates() {
        return rangeCoordinatesTextField.getText();
    }

    public String getRangeName() {
        return rangeNameTextField.getText();
    }

    @FXML
    void addButtonAction(ActionEvent event) {
        boolean isAdded = mainController.addRange(rangeNameTextField.getText(), rangeCoordinatesTextField.getText());

        if(isAdded) {
            confirmed = true;
            dialogStage.close();
        }
    }

    @FXML
    void CancelButtonAction(ActionEvent event) {
        dialogStage.close();
    }

}
