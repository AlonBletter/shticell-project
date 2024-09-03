package gui.singlecell;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SingleCellController extends CellModel {

    @FXML private Label valueLabel;

    public SingleCellController() {
        super("");
    }

    @FXML
    private void initialize() {
        valueLabel.textProperty().bind(value);
    }
}