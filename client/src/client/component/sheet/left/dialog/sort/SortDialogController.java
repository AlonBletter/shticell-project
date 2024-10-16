package client.component.sheet.left.dialog.sort;

import client.component.sheet.app.SheetController;
import client.component.sheet.common.ShticellResourcesConstants;
import client.component.sheet.left.dialog.sort.columnpicker.ColumnPickerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SortDialogController {
    @FXML private Button cancelButton;
    @FXML private TextField rangeCoordinatesTextField;
    @FXML private VBox columnsAddVBox;
    @FXML private Button sortButton;

    private Stage dialogStage;
    private SheetController mainController;
    private List<ColumnPickerController> columnPickers = new LinkedList<>();
    private int columnNumber = 0;

    @FXML
    void initialize() {
        rangeCoordinatesTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sortButton.fire();
            }
        });
    }

    public void addColumnPicker() {
        columnNumber++;

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ShticellResourcesConstants.COLUMN_PICKER_URL);
            Node columnPicker = loader.load();
            ColumnPickerController controller = loader.getController();

            controller.setColumnCounter(columnNumber);
            controller.setDialogController(this);
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
    void sortButtonAction(ActionEvent event) {
        String rangeToSort = rangeCoordinatesTextField.getText();

        List<String> selectedColumns = new ArrayList<>();

        for (ColumnPickerController controller : columnPickers) {
            String selectedColumn = controller.getSelectedColumn();
            selectedColumns.add(selectedColumn);
        }

        mainController.sortRange(rangeToSort, selectedColumns);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainController(SheetController mainController) {
        this.mainController = mainController;
    }
}
