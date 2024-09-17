package gui.left;

import gui.app.AppController;
import gui.common.ShticellResourcesConstants;
import gui.left.dimensiondialog.DimensionDialogController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LeftController {
    @FXML private Button columnWidthButton;
    @FXML private Button rowHeightButton;
    @FXML private ToggleButton centerAlignToggle;
    @FXML private ToggleButton leftAlignToggle;
    @FXML private ToggleButton rightAlignToggle;
    @FXML private HBox alignTogglesHBox;
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private ColorPicker textColorPicker;



    private AppController mainController;
    private Stage primaryStage;
    private SimpleDoubleProperty currentColumnWidthUnits;
    private SimpleDoubleProperty currentRowHeightUnits;
    private ToggleGroup alignmentToggleGroup;

    public LeftController() {
        currentColumnWidthUnits = new SimpleDoubleProperty();
        currentRowHeightUnits = new SimpleDoubleProperty();
        alignmentToggleGroup = new ToggleGroup();
    }

    @FXML
    private void initialize() {
        leftAlignToggle.setToggleGroup(alignmentToggleGroup);
        centerAlignToggle.setToggleGroup(alignmentToggleGroup);
        rightAlignToggle.setToggleGroup(alignmentToggleGroup);

        alignmentToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (oldToggle != null) {
                oldToggle.setSelected(false);
            }

            if (newToggle != null) {
                newToggle.setSelected(true);
            }
        });
    }

    @FXML
    void centerAlignToggleAction(ActionEvent event) {
        mainController.alignColumnCells(Pos.CENTER);
    }

    @FXML
    void leftAlignToggleAction(ActionEvent event) {
        mainController.alignColumnCells(Pos.CENTER_LEFT);
    }

    @FXML
    void rightAlignToggleAction(ActionEvent event) {
        mainController.alignColumnCells(Pos.CENTER_RIGHT);
    }

    @FXML
    void rowHeightButtonAction(ActionEvent event) {
        showDimensionDialog("Row Height", mainController.getCurrentSelectedRowHeight());
    }

    @FXML
    void columnWidthButtonAction(ActionEvent event) {
        showDimensionDialog("Column Width", mainController.getCurrentSelectedColumnWidth());
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
        leftAlignToggle.disableProperty().bind(mainController.isFileLoadedProperty().not());
        centerAlignToggle.disableProperty().bind(mainController.isFileLoadedProperty().not());
        rightAlignToggle.disableProperty().bind(mainController.isFileLoadedProperty().not());
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void updateView(Pos cellCurrentAlignment) {
        if(cellCurrentAlignment == Pos.CENTER_LEFT) {
            leftAlignToggle.setSelected(true);
        } else if (cellCurrentAlignment == Pos.CENTER) {
            centerAlignToggle.setSelected(true);
        } else {
            rightAlignToggle.setSelected(true);
        }
    }
}
