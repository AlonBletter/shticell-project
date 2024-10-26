package client.component.sheet.left.dialog.range;

import client.component.sheet.app.SheetController;
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
    private SheetController mainController;
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

    public void setMainController(SheetController mainController) {
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
        mainController.addRange(rangeNameTextField.getText(), rangeCoordinatesTextField.getText());
        dialogStage.close();
    }

    @FXML
    void cancelButtonAction(ActionEvent event) {
        dialogStage.close();
    }

}
