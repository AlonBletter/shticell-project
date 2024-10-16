package gui.left.dialog.graph;

import gui.app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class GraphController {
    @FXML private RadioButton barChartRadioButton;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private RadioButton lineChartRadioButton;
    @FXML private TextField xAxisTextField;
    @FXML private TextField yAxisTextField;

    private AppController mainController;


    @FXML
    public void initialize() {
        confirmButton.setDisable(true);

        barChartRadioButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                lineChartRadioButton.setSelected(false);

                if(confirmButton.isDisable()) {
                    confirmButton.setDisable(false);
                }
            }
        });

        lineChartRadioButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                barChartRadioButton.setSelected(false);

                if(confirmButton.isDisable()) {
                    confirmButton.setDisable(false);
                }
            }
        });

        xAxisTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });

        yAxisTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });

        barChartRadioButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });

        lineChartRadioButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });
    }

    @FXML
    void cancelButtonListener(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void confirmButtonListener(ActionEvent event) {
        String xAxisRange = xAxisTextField.getText();
        String yAxisRange = yAxisTextField.getText();
        boolean isBarChart = barChartRadioButton.isSelected();
        String chartType = isBarChart ? "BarChart" : "LineChart";

        mainController.createGraph(xAxisRange, yAxisRange, chartType);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
