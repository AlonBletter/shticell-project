package client.component.sheet.left.dialog.sort.columnpicker;

import client.component.sheet.left.dialog.sort.SortDialogController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class ColumnPickerController {
    @FXML private Button addColumnButton;
    @FXML private Label columnCounterLabel;
    @FXML private ChoiceBox<String> columnLetterChoiceBox;
    private SortDialogController dialogController;

    @FXML
    void initialize() {

    }

    @FXML
    void addColumnButton(ActionEvent event) {
        dialogController.addColumnPicker();
    }

    public void setDialogController(SortDialogController dialogController) {
        this.dialogController = dialogController;
    }

    public void setColumnCounter(int columnNumber) {
        columnCounterLabel.setText(columnNumber + ")");
    }

    public void setColumnRange(int numberOfColumns) {
        for (char current = 'A'; current < 'A' + numberOfColumns; current++) {
            columnLetterChoiceBox.getItems().add(String.valueOf(current));
        }

        if (!columnLetterChoiceBox.getItems().isEmpty()) {
            columnLetterChoiceBox.setValue(columnLetterChoiceBox.getItems().getFirst());
        }
    }

    public String getSelectedColumn() {
        return columnLetterChoiceBox.getSelectionModel().getSelectedItem();
    }
}
