package gui.header;

import engine.Engine;
import gui.app.AppController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

public class HeaderController {
    private AppController mainController;
    private Engine engine;

    @FXML private TextField actionLineLabel;
    @FXML private Label lastUpdatedCellVersionLabel;
    @FXML private Button loadFileButton;
    @FXML private Label loadedFilePathLabel;
    @FXML private Label originalCellValueLabel;
    @FXML private Label selectedCellIDLabel;
    @FXML private Label titleLabel;
    @FXML private Button updateValueButton;
    @FXML private ChoiceBox<?> versionSelectorChoiceBox;
    @FXML private Label versionNumberLabel;

    private SimpleStringProperty filePath;
    private SimpleStringProperty selectedCellCoordinate;
    private SimpleStringProperty cellOriginalValue;
    private SimpleIntegerProperty cellVersionNumber;

    private Stage primaryStage;

    public HeaderController() {
        filePath = new SimpleStringProperty();
        selectedCellCoordinate = new SimpleStringProperty();
        cellOriginalValue = new SimpleStringProperty();
        cellVersionNumber = new SimpleIntegerProperty();
    }

    @FXML
    private void initialize() {
        loadedFilePathLabel.textProperty().bind(filePath);
        selectedCellIDLabel.textProperty().bind(selectedCellCoordinate);
        originalCellValueLabel.textProperty().bind(cellOriginalValue);
        lastUpdatedCellVersionLabel.textProperty().bind(Bindings.format("%d", cellVersionNumber));
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void loadFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select words file");
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

    }




}
