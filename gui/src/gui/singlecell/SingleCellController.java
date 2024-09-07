package gui.singlecell;

import engine.sheet.coordinate.Coordinate;
import gui.app.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SingleCellController extends CellModel {
    private Coordinate coordinate;
    private AppController mainController;

    @FXML private AnchorPane cellPane;
    @FXML private Label valueLabel;


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @FXML
    void onCellClickUpdate(MouseEvent event) {
        if (mainController != null) {
            mainController.updateHeaderOnCellClick(this);
        }
    }

    public SingleCellController() {
        super("");
    }

    @FXML
    private void initialize() {
        valueLabel.textProperty().bind(effectiveValue);
    }
}