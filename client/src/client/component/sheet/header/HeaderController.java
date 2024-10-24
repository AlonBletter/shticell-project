package client.component.sheet.header;

import client.component.sheet.app.SheetController;
import client.component.sheet.center.singlecell.CellModel;
import client.component.sheet.common.ShticellResourcesConstants;
import engine.sheet.coordinate.Coordinate;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;

public class HeaderController {
    private SheetController mainController;

    @FXML private TextField actionLineTextField;
    @FXML private Label lastUpdatedCellVersionLabel;
    @FXML private Label originalCellValueLabel;
    @FXML private Label selectedCellIDLabel;
    @FXML private Label titleLabel;
    @FXML private Button updateValueButton;
    @FXML private ComboBox<String> versionSelectorComboBox;
    @FXML private GridPane headerGridPane;
    @FXML private ChoiceBox<String> skinSelectorChoiceBox;
    @FXML private ChoiceBox<String> animationButton;
    @FXML private Tooltip originalValueToolTip;

    private final SimpleBooleanProperty isFileLoaded;
    private final ObservableList<String> versionNumberList;
    private Coordinate selectedCellCoordinate;
    private Stage primaryStage;

    public HeaderController() {
        isFileLoaded = new SimpleBooleanProperty(false);
        versionNumberList = FXCollections.observableArrayList();
    }

    public void setMainController(SheetController mainController) {
        this.mainController = mainController;
        actionLineTextField.disableProperty().bind(mainController.readonlyPresentationProperty());
        updateValueButton.disableProperty().bind(mainController.readonlyPresentationProperty());
    }

    @FXML
    private void initialize() {
        originalValueToolTip.textProperty().bind(originalCellValueLabel.textProperty());
        originalCellValueLabel.setOnMouseEntered(event -> {
            if (isTextTruncated(originalCellValueLabel)) {
                Tooltip.install(originalCellValueLabel, originalValueToolTip);
            } else {
                Tooltip.uninstall(originalCellValueLabel, originalValueToolTip);
            }
        });
        originalCellValueLabel.setOnMouseExited(event -> Tooltip.uninstall(originalCellValueLabel, originalValueToolTip));

        ObservableList<String> skins = FXCollections.observableArrayList("Default", "Blue", "Red");
        skinSelectorChoiceBox.setItems(skins);
        skinSelectorChoiceBox.getSelectionModel().selectFirst();
        skinSelectorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                mainController.setComponentsSkin(newValue)
        );

        ObservableList<String> animations = FXCollections.observableArrayList("No Animations", "Dancing Cells", "New Value Fade");
        animationButton.setItems(animations);
        animationButton.getSelectionModel().selectFirst();
        animationButton.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mainController.setAnimations(newValue);
        });

        initializeVersionSelectorComboBox();

        actionLineTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                updateValueButton.fire();
            }
        });
    }

    private boolean isTextTruncated(Label label) {
        return label.getWidth() < label.getFont().getSize() * label.getText().length();
    }

    private void initializeVersionSelectorComboBox() {
        versionSelectorComboBox.setItems(versionNumberList);

        versionSelectorComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || !newValue.matches("")) {
                versionSelectorComboBox.getEditor().setText(newValue.replaceAll("\\D", ""));
            }
        });

        versionSelectorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldVersion, newVersion) -> {
            if (newVersion != null && !newVersion.isEmpty()) {
                int versionToLoadNumber = Integer.parseInt(newVersion);
                mainController.displaySheetByVersion(versionToLoadNumber);
                versionSelectorComboBox.getSelectionModel().selectFirst();
            }
        });

        versionSelectorComboBox.getEditor().setOnAction(event -> {
            String input = versionSelectorComboBox.getEditor().getText();
            if (input != null && !input.isEmpty()) {
                int versionToLoadNumber = Integer.parseInt(input);
                mainController.displaySheetByVersion(versionToLoadNumber);
                versionSelectorComboBox.getSelectionModel().selectFirst();
            }
        });
    }

    private void clearDataFromHeader() {
        lastUpdatedCellVersionLabel.textProperty().unbind();
        selectedCellIDLabel.textProperty().unbind();
        originalCellValueLabel.textProperty().unbind();
        lastUpdatedCellVersionLabel.setText("");
        selectedCellIDLabel.setText("");
        originalCellValueLabel.setText("");
    }
    // maybe unite the both ^ V
    public void initializeHeaderAfterLoad() {
        clearDataFromHeader();
        versionNumberList.clear();
        versionNumberList.addFirst("");
        addVersionNumbers();
        versionSelectorComboBox.getSelectionModel().selectFirst();
    }

    private void addVersionNumbers() {
        int currentVersion = mainController.getVersion();

        for(int i = 1 ; i <= currentVersion ; i++) {
            versionNumberList.add(String.valueOf(i));
        }
    }

    public void updateHeader() {
        actionLineTextField.setText("");
        versionNumberList.add(String.valueOf(mainController.getVersion()));

//        if (mainController.getVersion() != 1) {
//        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void updateValueButtonAction() {
        if(selectedCellCoordinate == null) {
            throw new IllegalArgumentException("Please select a cell before updating value.");
        }

        mainController.updateCell(selectedCellCoordinate, actionLineTextField.getText());
    }

    public void updateHeaderCellData(CellModel selectedCell) {
        selectedCellCoordinate = selectedCell.getCoordinate();
        selectedCellIDLabel.setText(selectedCellCoordinate.toString());
        originalCellValueLabel.textProperty().bind(selectedCell.originalValueProperty());
        lastUpdatedCellVersionLabel.textProperty().bind(selectedCell.lastModifiedVersionProperty());
    }

    public void requestActionLineFocus() {
        actionLineTextField.requestFocus();
    }

    public void setSkin(String skinType) {
        switch (skinType) {
            case "Blue":
                applyCSS(ShticellResourcesConstants.BLUE_HEADER_CSS_URL);
                break;
            case "Red":
                applyCSS(ShticellResourcesConstants.RED_HEADER_CSS_URL);
                break;
            case "Default":
                applyCSS(ShticellResourcesConstants.DEFAULT_HEADER_CSS_URL);
                break;
        }
    }

    private void applyCSS(URL cssURL) {
        headerGridPane.getStylesheets().clear();
        headerGridPane.getStylesheets().add(cssURL.toExternalForm());
    }
}
