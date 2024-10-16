package client.component.sheet.left.dialog.dimension;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class DimensionDialogController {
    @FXML private Button applyButton;
    @FXML private Button cancelButton;
    @FXML private Label dimensionLabel;
    @FXML private TextField valueTextField;

    private Double result;  // Store the result entered by the user

    @FXML
    void applyButtonAction(ActionEvent event) {
        String value = valueTextField.getText();
        if (value.isEmpty() || !isNumeric(value)) {
            return;
        }

        result = Double.parseDouble(value);

        if(result <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Value");
            alert.setHeaderText("An error occurred while changing dimension value");
            alert.setContentText("Dimension must be a positive number");

            alert.showAndWait();
            valueTextField.requestFocus();
            return;
        }

        Stage stage = (Stage) applyButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void cancelButtonAction(ActionEvent event) {
        result = null;  // User canceled, so no valid result
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void setDimensionType(String dimensionType) {
        dimensionLabel.setText(dimensionType + ":");
    }

    public void setInitialValue(double initialValue) {
        if (initialValue % 1 == 0) {
            valueTextField.setText(String.valueOf((int) initialValue));
        } else {
            valueTextField.setText(String.valueOf(initialValue));
        }
    }

    public Double getResult() {
        return result;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    void initialize() {
        // Custom TextFormatter to accept both integers and decimals
        valueTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            // Regular expression to allow both integers and decimals
            if (newText.matches("-?\\d*([\\.]\\d*)?")) {
                return change;
            }
            return null;
        }));

        valueTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                applyButton.fire();
            }
        });
    }
}
