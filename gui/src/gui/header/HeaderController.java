package gui.header;

import engine.Engine;
import engine.sheet.coordinate.Coordinate;
import gui.app.AppController;
import gui.singlecell.CellModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HeaderController {
    private AppController mainController;
    private Engine engine;

    @FXML private TextField actionLineTextField;
    @FXML private Label lastUpdatedCellVersionLabel;
    @FXML private Button loadFileButton;
    @FXML private Label loadedFilePathLabel;
    @FXML private Label originalCellValueLabel;
    @FXML private Label selectedCellIDLabel;
    @FXML private Label titleLabel;
    @FXML private Button updateValueButton;
    @FXML private ComboBox<String> versionSelectorComboBox;
    @FXML private ProgressBar loadFileProgressBar;

    private SimpleStringProperty filePath;
    private SimpleBooleanProperty isFileLoaded;
    private Coordinate selectedCellCoordinate;
    private Stage primaryStage;

    public HeaderController() {
        filePath = new SimpleStringProperty();
        isFileLoaded = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize() {
        loadedFilePathLabel.textProperty().bind(filePath);
        actionLineTextField.disableProperty().bind(isFileLoaded.not());
        updateValueButton.disableProperty().bind(isFileLoaded.not());
        versionSelectorComboBox.disableProperty().bind(isFileLoaded.not());
        initializeVersionSelectorComboBox();
    }

    private void initializeVersionSelectorComboBox() {
        versionSelectorComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                versionSelectorComboBox.getEditor().setText(newValue.replaceAll("\\D", ""));
            }
        });

        versionSelectorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldVersion, newVersion) -> {
            if (newVersion != null && !newVersion.isEmpty()) {
                int version = Integer.parseInt(newVersion.toString());
                mainController.displaySheetByVersion(version);
            }
        });
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void loadFileButtonAction() {
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
            throw new IllegalArgumentException("No cell was selected.");
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

    private List<String> getAvailableVersionsFromEngine() {
        return IntStream.rangeClosed(1, mainController.getSheetCurrentVersion())
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
    }

    public void enableButtonsAfterLoad() {
        isFileLoaded.set(true);
        versionSelectorComboBox.setItems(FXCollections.observableArrayList(getAvailableVersionsFromEngine()));
    }

    public void requestActionLineFocus() {
        actionLineTextField.requestFocus();
    }

    public void increaseComboBoxVersion() { //TODO find a better way...
        versionSelectorComboBox.setItems(FXCollections.observableArrayList(getAvailableVersionsFromEngine()));
    }
}
