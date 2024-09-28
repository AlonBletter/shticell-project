package gui.left.dialog.graph;

import gui.app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GraphController {

    @FXML
    private RadioButton barChartRadioButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    @FXML
    private RadioButton lineChartRadioButton;

    @FXML
    private TextField xAxisTextField;

    @FXML
    private TextField yAxisTextField;

    private AppController mainController;


    @FXML
    public void initialize() {
        barChartRadioButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                lineChartRadioButton.setSelected(false);
            }
        });

        lineChartRadioButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                barChartRadioButton.setSelected(false);
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
        boolean isLineChart = lineChartRadioButton.isSelected();
        String chartType = isBarChart ? "BarChart" : "LineChart";

        mainController.createGraph(xAxisRange, yAxisRange, chartType);

    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
