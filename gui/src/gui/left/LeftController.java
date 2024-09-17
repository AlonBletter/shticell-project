package gui.left;

import gui.app.AppController;
import gui.common.ShticellResourcesConstants;
import gui.left.dimensiondialog.DimensionDialogController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LeftController {
    @FXML private Button columnWidthButton;
    @FXML private Button rowHeightButton;

    private AppController mainController;
    private Stage primaryStage;
    private SimpleDoubleProperty currentColumnWidthUnits;
    private SimpleDoubleProperty currentRowHeightUnits;

    public LeftController() {
        currentColumnWidthUnits = new SimpleDoubleProperty();
        currentRowHeightUnits = new SimpleDoubleProperty();
    }

    @FXML
    private void initialize() {

    }

    @FXML
    void rowHeightButtonAction(ActionEvent event) {
        showDimensionDialog("Row Height", currentRowHeightUnits.getValue());
    }

    @FXML
    void columnWidthButtonAction(ActionEvent event) {
        showDimensionDialog("Column Width", currentColumnWidthUnits.getValue());
    }

    private void showDimensionDialog(String dimensionType, double dimensionToDisplay) {
        try {
            FXMLLoader loader = new FXMLLoader(ShticellResourcesConstants.DIMENSION_DIALOG_URL);
            Parent root = loader.load();

            DimensionDialogController dialogController = loader.getController();

            dialogController.setDimensionType(dimensionType);
            dialogController.setInitialValue(dimensionToDisplay);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Set " + dimensionType);
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.showAndWait();

            Double result = dialogController.getResult();
            if (result != null) {
                System.out.println("User entered " + dimensionType + ": " + result);

                if (dimensionType.equals("Column Width")) {
                    mainController.updateColumnWidth(result);
                } else if (dimensionType.equals("Row Height")) {
                    mainController.updateRowHeight(result);
                }

                dialogStage.close();
            }
        } catch (IOException e) {
                e.printStackTrace(); // Handle the exception as appropriate for your application
        }
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        columnWidthButton.disableProperty().bind(mainController.isFileLoadedProperty().not());
        rowHeightButton.disableProperty().bind(mainController.isFileLoadedProperty().not());
        currentColumnWidthUnits.bind(mainController.columnWidthUnitsProperty());
        currentRowHeightUnits.bind(mainController.rowHeightUnitsProperty());
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
