package gui.header;

import dto.CellDTO;
import engine.Engine;
import gui.app.AppController;
import gui.singlecell.CellModel;
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

    private Stage primaryStage;

    public HeaderController() {
        filePath = new SimpleStringProperty();
    }

    @FXML
    private void initialize() {
        loadedFilePathLabel.textProperty().bind(filePath);
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

    public void updateHeaderData(CellDTO selectedCell) {
        selectedCellIDLabel.setText(selectedCell.coordinate().toString());
        originalCellValueLabel.setText(selectedCell.originalValue());
        lastUpdatedCellVersionLabel.setText(String.valueOf(selectedCell.lastModifiedVersion()));
    }
}
