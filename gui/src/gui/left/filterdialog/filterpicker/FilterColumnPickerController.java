package gui.left.filterdialog.filterpicker;

import gui.left.filterdialog.FilterDialogController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class FilterColumnPickerController {
    @FXML private Button addColumnButton;
    @FXML private Label columnCounterLabel;
    @FXML private ChoiceBox<String> columnLetterChoiceBox;
    private FilterDialogController dialogController;
    private List<String> pickedValues = new ArrayList<>();
    private Stage dialogStage;

    @FXML
    void initialize() {
        columnLetterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                ListView<String> listView = new ListView<>();
                Button button = new Button("Apply");
                List<String> columnUniqueValues = dialogController.getColumnUniqueValues(newValue);
                listView.getItems().addAll(columnUniqueValues);
                listView.setCellFactory(CheckBoxListCell.forListView(item -> {
                    BooleanProperty observableBoolean = new SimpleBooleanProperty();
                    observableBoolean.addListener((obs, wasSelected, isNowSelected) -> {
                        if (isNowSelected) {
                            pickedValues.add(item);
                        } else {
                            pickedValues.remove(item);
                        }
                    });
                    return observableBoolean;
                }));

                button.setOnAction(e -> {
                    for (int i = 0; i < pickedValues.size(); i++) {
                        System.out.println(pickedValues.get(i));
                    }
                });

                VBox root = new VBox();
                root.setAlignment(Pos.CENTER);
                root.getChildren().addAll(listView, button);

                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(dialogStage);
                stage.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void addColumnButton(ActionEvent event) {
        dialogController.addColumnPicker();
    }

    public void setDialogController(FilterDialogController dialogController) {
        this.dialogController = dialogController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
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

    public List<String> getPickedValues() {
        return pickedValues;
    }
}