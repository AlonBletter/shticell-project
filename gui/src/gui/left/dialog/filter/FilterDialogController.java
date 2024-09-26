package gui.left.dialog.filter;

import gui.app.AppController;
import gui.common.ShticellResourcesConstants;
import gui.left.dialog.filter.filtercolumnpicker.FilterColumnPickerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FilterDialogController {
    @FXML private Button cancelButton;
    @FXML private VBox columnsAddVBox;
    @FXML private Button filterButton;
    @FXML private TextField rangeCoordinatesTextField;
    private int columnNumber = 0;
    private AppController mainController;
    private Stage dialogStage;
    private List<FilterColumnPickerController> columnPickers = new LinkedList<>();

    @FXML
    void initialize() {

    }

    public void addColumnPicker() {
        columnNumber++;

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ShticellResourcesConstants.FILTER_PICKER_URL);
            Node columnPicker = loader.load();
            FilterColumnPickerController controller = loader.getController();

            controller.setColumnCounter(columnNumber);
            controller.setDialogController(this);
            controller.setDialogStage(dialogStage);
            controller.setColumnRange(mainController.getNumberOfColumns());
            columnsAddVBox.getChildren().add(columnPicker);
            columnPickers.add(controller);

        } catch (IOException e) {
            throw new RuntimeException("IOException Occurred...");
        }
    }

    @FXML
    void cancelButtonAction(ActionEvent event) {
        dialogStage.close();
    }

    @FXML
    void filterButtonAction(ActionEvent event) {
        String rangeToFilter = rangeCoordinatesTextField.getText();
        Map<String, List<String>> filterRequestValues = new HashMap<>();

        for(FilterColumnPickerController controller : columnPickers) {
            String selectedColumn = controller.getSelectedColumn();
            List<String> selectedValues = controller.getPickedValues();
            //!selectedValues.isEmpty() &&
            if (!selectedColumn.isEmpty()) {
                filterRequestValues.put(selectedColumn, selectedValues);
            }
        }

        if (!filterRequestValues.isEmpty()) {
            mainController.filterRange(rangeToFilter, filterRequestValues);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("No filter was selected!\nPlease pick values for at least one column.");
            alert.showAndWait();
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public List<String> getColumnUniqueValues(String columnLetter) {
        return mainController.getColumnUniqueValues(columnLetter);
    }
}
