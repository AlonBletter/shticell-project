package gui.header;

import dto.CellDTO;
import engine.Engine;
import engine.sheet.coordinate.Coordinate;
import gui.app.AppController;
import gui.singlecell.CellModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HeaderController {
    private AppController mainController;

    @FXML private TextField actionLineTextField;
    @FXML private Label lastUpdatedCellVersionLabel;
    @FXML private Button loadFileButton;
    @FXML private Label loadedFilePathLabel;
    @FXML private Label originalCellValueLabel;
    @FXML private Label selectedCellIDLabel;
    @FXML private Label titleLabel;
    @FXML private Button updateValueButton;
    @FXML private ComboBox<String> versionSelectorComboBox;

    private SimpleStringProperty filePath;
    private SimpleBooleanProperty isFileLoaded;
    private ObservableList<String> versionNumberList;
    private Coordinate selectedCellCoordinate;
    private Stage primaryStage;

    public HeaderController() {
        filePath = new SimpleStringProperty("File Path");
        isFileLoaded = new SimpleBooleanProperty(false);
        versionNumberList = FXCollections.observableArrayList();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        isFileLoaded.bind(mainController.isFileLoadedProperty());
    }

    @FXML
    private void initialize() {
        loadedFilePathLabel.textProperty().bind(filePath);
        actionLineTextField.disableProperty().bind(isFileLoaded.not());
        updateValueButton.disableProperty().bind(isFileLoaded.not());
        initializeVersionSelectorComboBox();

        actionLineTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                updateValueButton.fire(); // Trigger the button action
            }
        });
    }

    private void initializeVersionSelectorComboBox() {
        versionSelectorComboBox.setItems(versionNumberList);
        versionSelectorComboBox.disableProperty().bind(isFileLoaded.not());

        versionSelectorComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                versionSelectorComboBox.getEditor().setText(newValue.replaceAll("\\D", ""));
            }
        });

        versionSelectorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldVersion, newVersion) -> {
            if (newVersion != null && !newVersion.isEmpty()) {
                int versionToLoadNumber = Integer.parseInt(newVersion);
                mainController.displaySheetByVersion(versionToLoadNumber);
            }
        });

        versionSelectorComboBox.getEditor().setOnAction(event -> {
            String input = versionSelectorComboBox.getEditor().getText();
            if (input != null && !input.isEmpty()) {
                int versionToLoadNumber = Integer.parseInt(input);
                mainController.displaySheetByVersion(versionToLoadNumber);
                versionSelectorComboBox.getSelectionModel().clearSelection();
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


    // TODO change this maybe?
    public void enableButtonsAfterLoad() {
        //isFileLoaded.set(true);
        versionSelectorComboBox.getItems().clear();
        refreshComboBoxVersion();
    }

    public void refreshComboBoxVersion() { //TODO find a better way...
        versionNumberList.add(String.valueOf(mainController.getSheetCurrentVersion()));
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void loadFileButtonAction() {
        clearDataFromHeader();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
        filePath.set(absolutePath);
        mainController.loadFile(absolutePath);
    }

    @FXML
    public void updateValueButtonAction() {
        if(selectedCellCoordinate == null) {
            throw new IllegalArgumentException("Please select a cell before updating value.");
        }

        mainController.updateCell(selectedCellCoordinate, actionLineTextField.getText());
        actionLineTextField.setText("");
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
}
