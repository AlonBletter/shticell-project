package client.component.sheet.left.dialog.filter.filtercolumnpicker;

import client.component.sheet.left.dialog.filter.FilterDialogController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class FilterColumnPickerController {
    @FXML private Button addColumnButton;
    @FXML private Label columnCounterLabel;
    @FXML private ChoiceBox<String> columnLetterChoiceBox;
    @FXML private Button pickValuesButton;

    private FilterDialogController dialogController;
    private final List<String> pickedValues = new ArrayList<>();
    private Stage dialogStage;
    private String currentColumn;
    private boolean firstTime = true;
    private final SimpleBooleanProperty columnSelected = new SimpleBooleanProperty(false);

    @FXML
    void initialize() {
        columnLetterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                pickedValues.clear();
                currentColumn = newValue;
                columnSelected.set(true);
                firstTime = true;
            } else {
                columnSelected.set(false);
            }
        });

        pickValuesButton.disableProperty().bind(columnSelected.not());
    }

    @FXML
    void pickValuesButtonAction(ActionEvent event) {
        ListView<String> listView = new ListView<>();
        Button applyButton = new Button("Apply");
        Button selectAllButton = new Button("Select All");
        Button deselectAllButton = new Button("Deselect All");

        List<String> columnUniqueValues = dialogController.getColumnUniqueValues(currentColumn);
        listView.getItems().addAll(columnUniqueValues);

        if (pickedValues.isEmpty() && firstTime) {
            pickedValues.addAll(columnUniqueValues);
            firstTime = false;
        }

        listView.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty selectedProperty = new SimpleBooleanProperty(pickedValues.contains(item));

            selectedProperty.addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    if (!pickedValues.contains(item)) {
                        pickedValues.add(item);
                    }
                } else {
                    pickedValues.remove(item);
                }
            });

            return selectedProperty;
        }));

        HBox buttons = new HBox(5);
        buttons.getChildren().addAll(selectAllButton, deselectAllButton, applyButton);

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(new Label("Select Values"), listView, buttons);

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(dialogStage);
        stage.setScene(new Scene(root, 300, 400));
        stage.setResizable(false);

        applyButton.setOnAction(e -> stage.close());
        selectAllButton.setOnAction(e -> {
            pickedValues.clear();
            pickedValues.addAll(columnUniqueValues);
            listView.refresh();
        });
        deselectAllButton.setOnAction(e -> {
            pickedValues.clear();
            listView.refresh();
        });
        root.setOnKeyPressed(e -> applyButton.fire());

        stage.showAndWait();
    }

    @FXML
    void addColumnButtonAction(ActionEvent event) {
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

        columnLetterChoiceBox.getItems().addFirst("");
        columnLetterChoiceBox.getSelectionModel().selectFirst();
    }

    public String getSelectedColumn() {
        return columnLetterChoiceBox.getSelectionModel().getSelectedItem();
    }

    public List<String> getPickedValues() {
        return pickedValues;
    }
}