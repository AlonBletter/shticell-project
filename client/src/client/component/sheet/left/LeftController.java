package client.component.sheet.left;

import dto.CellDTO;
import engine.sheet.cell.api.CellType;
import engine.sheet.range.Range;
import client.component.sheet.app.SheetController;
import client.component.sheet.common.ShticellResourcesConstants;
import client.component.sheet.left.dialog.dimension.DimensionDialogController;
import client.component.sheet.left.dialog.filter.FilterDialogController;
import client.component.sheet.left.dialog.graph.GraphController;
import client.component.sheet.left.dialog.range.RangeDialogController;
import client.component.sheet.left.dialog.sort.SortDialogController;
import client.component.sheet.left.dialog.whatif.WhatIfDialogController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class LeftController {
    @FXML private Button columnWidthButton;
    @FXML private Button rowHeightButton;
    @FXML private ToggleButton centerAlignToggle;
    @FXML private ToggleButton leftAlignToggle;
    @FXML private ToggleButton rightAlignToggle;
    @FXML private HBox alignTogglesHBox;
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private ColorPicker textColorPicker;
    @FXML private Button resetStylingButton;
    @FXML private ListView<String> rangesListView;
    @FXML private Button addRangeButton;
    @FXML private Button deleteRangeButton;
    @FXML private Button viewRangeButton;
    @FXML private Button sortButton;
    @FXML private Button filterButton;
    @FXML private VBox leftVBox;
    @FXML private Button whatIfButton;
    @FXML private Button createGraphButton;

    private SheetController mainController;
    private Stage primaryStage;
    private ToggleGroup alignmentToggleGroup;

    public LeftController() {
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

        backgroundColorPicker.setOnAction(event -> {
            Color selectedColor = backgroundColorPicker.getValue();
            mainController.updateCellBackgroundColor(toHexString(selectedColor));
        });

        textColorPicker.setOnAction(event -> {
            Color selectedColor = textColorPicker.getValue();
            mainController.updateCellTextColor(toHexString(selectedColor));
        });

        resetStylingButton.setOnAction(event -> {
           mainController.updateCellBackgroundColor(null);
           mainController.updateCellTextColor(null);
        });

        deleteRangeButton.disableProperty().bind(rangesListView.getSelectionModel().selectedItemProperty().isNull());
        viewRangeButton.disableProperty().bind(rangesListView.getSelectionModel().selectedItemProperty().isNull());

    }

    @FXML
    void addRangeButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(ShticellResourcesConstants.RANGE_DIALOG_URL);
            Parent root = loader.load();

            RangeDialogController dialogController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Range");
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogController.setDialogStage(dialogStage);
            dialogController.setMainController(mainController);
            dialogStage.showAndWait();

            if (dialogController.isConfirmed()) {
                rangesListView.getItems().add(dialogController.getRangeName());
            }
            dialogStage.close();

        } catch (IOException e) {
            throw new RuntimeException("IO Exception occurred...");
        }
    }

    @FXML
    void deleteRangeButtonAction(ActionEvent event) {
        boolean deleted = mainController.deleteRange(rangesListView.getSelectionModel().getSelectedItem());

        if (deleted) {
            rangesListView.getItems().remove(rangesListView.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    void viewRangeButtonAction(ActionEvent event) {
        mainController.viewRange(rangesListView.getSelectionModel().getSelectedItem());
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
                if (dimensionType.equals("Column Width")) {
                    mainController.updateColumnWidth(result);
                } else if (dimensionType.equals("Row Height")) {
                    mainController.updateRowHeight(result);
                }

                dialogStage.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("IO Exception occurred...");
        }
    }


    @FXML
    void sortButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(ShticellResourcesConstants.SORT_DIALOG_URL);
            Parent root = loader.load();

            SortDialogController sortDialogController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sort");
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            sortDialogController.setDialogStage(dialogStage);
            sortDialogController.setMainController(mainController);
            sortDialogController.addColumnPicker();
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("IO Exception occurred...");
        }
    }

    @FXML
    void filterButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(ShticellResourcesConstants.FILTER_DIALOG_URL);
            Parent root = loader.load();

            FilterDialogController dialogController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Filter");
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogController.setDialogStage(dialogStage);
            dialogController.setMainController(mainController);
            dialogController.addColumnPicker();
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("IO Exception occurred...");
        }
    }

    public void setMainController(SheetController mainController) {
        this.mainController = mainController;
        SimpleBooleanProperty mainIsFileLoadedProperty = mainController.isFileLoadedProperty();
        columnWidthButton.disableProperty().bind(mainIsFileLoadedProperty.not());
        rowHeightButton.disableProperty().bind(mainIsFileLoadedProperty.not());
        leftAlignToggle.disableProperty().bind(mainIsFileLoadedProperty.not());
        centerAlignToggle.disableProperty().bind(mainIsFileLoadedProperty.not());
        rightAlignToggle.disableProperty().bind(mainIsFileLoadedProperty.not());
        backgroundColorPicker.disableProperty().bind(mainIsFileLoadedProperty.not());
        textColorPicker.disableProperty().bind(mainIsFileLoadedProperty.not());
        resetStylingButton.disableProperty().bind(mainIsFileLoadedProperty.not());
        rangesListView.disableProperty().bind(mainIsFileLoadedProperty.not());
        addRangeButton.disableProperty().bind(mainIsFileLoadedProperty.not());
        sortButton.disableProperty().bind(mainIsFileLoadedProperty.not());
        filterButton.disableProperty().bind(mainIsFileLoadedProperty.not());
        whatIfButton.disableProperty().bind(mainIsFileLoadedProperty.not());
        createGraphButton.disableProperty().bind(mainIsFileLoadedProperty.not());
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

    public void loadRanges(List<Range> ranges) {
        rangesListView.getItems().clear();

        for(Range range : ranges) {
            rangesListView.getItems().add(range.getName());
        }
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void setSkin(String skinType) {
        switch (skinType) {
            case "Blue":
                applyCSS(Objects.requireNonNull(ShticellResourcesConstants.BLUE_LEFT_CSS_URL));
                break;
            case "Red":
                applyCSS(Objects.requireNonNull(ShticellResourcesConstants.RED_LEFT_CSS_URL));
                break;
            case "Default":
                applyCSS(Objects.requireNonNull(ShticellResourcesConstants.DEFAULT_LEFT_CSS_URL));
                break;
        }
    }

    private void applyCSS(URL cssURL) {
        leftVBox.getStylesheets().clear();
        leftVBox.getStylesheets().add(cssURL.toExternalForm());
    }

    @FXML
    void whatIfButtonAction(ActionEvent event) {
        CellDTO currentCell = mainController.getCurrentCell();
        if(currentCell.effectiveValue().cellType() != CellType.NUMERIC || currentCell.containsFunction()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.initOwner(primaryStage);
            alert.setContentText("Invalid cell selection: The selected cell must contain a pure (non-function) " +
                    "numeric value for the 'What If' feature.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(ShticellResourcesConstants.WHAT_IF_DIALOG_URL);
            Parent root = loader.load();

            WhatIfDialogController dialogController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("What-If");
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogController.setMainController(mainController);
            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("IO Exception occurred...");
        }
    }

    @FXML
    void createGraphListener(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ShticellResourcesConstants.GRAPH_DIALOG_URL);
            Parent parent = fxmlLoader.load();
            GraphController graphController = fxmlLoader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Graph");
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(parent));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            graphController.setMainController(mainController);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
