package client.component.sheet.left.dialog.whatif;

import client.component.sheet.app.SheetController;
import dto.coordinate.Coordinate;
import dto.coordinate.CoordinateFactory;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WhatIfDialogController {
    @FXML private Button createSliderButton;
    @FXML private Button editSliderButton;
    @FXML private TextField fromLimitTextField;
    @FXML private VBox sliderContainer;
    @FXML private TextField stepSizeTextField;
    @FXML private TextField toLimitTextField;
    @FXML private TextField coordinateTextField;

    private SheetController mainController;
    private Stage dialogStage;
    private final SimpleBooleanProperty isSliderCreated = new SimpleBooleanProperty(false);

    @FXML
    void initialize() {
        editSliderButton.disableProperty().bind(isSliderCreated.not());
        createSliderButton.disableProperty().bind(isSliderCreated);
        fromLimitTextField.disableProperty().bind(isSliderCreated);
        toLimitTextField.disableProperty().bind(isSliderCreated);
        stepSizeTextField.disableProperty().bind(isSliderCreated);
        coordinateTextField.disableProperty().bind(isSliderCreated);
    }

    @FXML
    void createSliderButtonAction(ActionEvent event) {
        int sheetNumberOfColumns = mainController.getNumberOfColumns();
        int sheetNumOfRows = mainController.getNumberOfRows();

        String fromText = fromLimitTextField.getText();
        String toText = toLimitTextField.getText();
        String stepText = stepSizeTextField.getText();
        String coordinateText = coordinateTextField.getText();

        if (isValidDouble(fromText) && isValidDouble(toText) && isValidDouble(stepText) && isValidCoordinate(coordinateText)) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(coordinateText);
            double fromLimit = Double.parseDouble(fromLimitTextField.getText());
            double toLimit = Double.parseDouble(toLimitTextField.getText());
            double stepSize = Double.parseDouble(stepSizeTextField.getText());

            if (fromLimit >= toLimit || stepSize <= 0 || stepSize > (toLimit - fromLimit)) {
                showErrorAlert("""
                        Invalid input detected.
                        Please ensure that:
                        - 'From' value is less than the 'To' value.
                        - Step size is positive and does not exceed the difference between 'From' and 'To'.
                        Re-enter the values correctly and try again.""");
                return;
            } else if(coordinate.row() > sheetNumOfRows || coordinate.row() < 1 ||
                        coordinate.column() > sheetNumberOfColumns || coordinate.column() < 1) {
                char sheetColumnRange = (char) (sheetNumberOfColumns + 'A' - 1);
                char cellColumnChar = (char) (coordinate.column() + 'A' - 1);

                String errorMessage = " Expected column between A-" + sheetColumnRange +
                        " and row between 1-" + sheetNumOfRows + ". " +
                        "But received column [" + cellColumnChar + "] and row [" + coordinate.row() + "].";
                showErrorAlert("Invalid coordinate.\n" + errorMessage);
                return;
            }

            Slider valueSlider = getSlider(fromLimit, toLimit, stepSize, coordinate);
            sliderContainer.getChildren().add(valueSlider);
            isSliderCreated.set(true);
        } else {
            showErrorAlert("Invalid input.\nAll fields must contain numbers. Please fill all the fields in the form.");
        }
    }

    private Slider getSlider(double fromLimit, double toLimit, double stepSize, Coordinate coordinate) {
        Slider valueSlider = new Slider();

        valueSlider.setMin(fromLimit);
        valueSlider.setMax(toLimit);
        valueSlider.setValue(fromLimit);
        valueSlider.setMinorTickCount(0);
        valueSlider.setMajorTickUnit(stepSize);
        valueSlider.setBlockIncrement(stepSize);
        valueSlider.setSnapToTicks(true);
        valueSlider.setShowTickLabels(true);
        valueSlider.setShowTickMarks(true);
        valueSlider.setMaxWidth(Double.MAX_VALUE);

        valueSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double snappedValue = Math.round(newValue.doubleValue() / stepSize) * stepSize;
            valueSlider.setValue(snappedValue);
            mainController.displayExpectedValue(snappedValue, coordinate);
        });

        return valueSlider;
    }

    private void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    @FXML
    void editSliderButtonAction(ActionEvent event) {
        sliderContainer.getChildren().clear();
        isSliderCreated.set(false);
        mainController.restoreCurrentValues();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;

        this.dialogStage.setOnCloseRequest(event -> mainController.restoreCurrentValues());
    }

    public void setMainController(SheetController mainController) {
        this.mainController = mainController;
    }

    private boolean isValidDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidCoordinate(String coordinate) {
        try {
            CoordinateFactory.createCoordinate(coordinate);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
