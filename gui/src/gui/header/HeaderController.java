package gui.header;

import dto.CellDTO;
import engine.Engine;
import engine.sheet.coordinate.Coordinate;
import gui.app.AppController;
import gui.singlecell.CellModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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
    @FXML private ChoiceBox<?> versionSelectorChoiceBox;
    @FXML private Label versionNumberLabel;

    private SimpleStringProperty filePath;
    private Coordinate selectedCellCoordinate;

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

    public void requestActionLineFocus() {
        actionLineTextField.requestFocus();
    }
}
