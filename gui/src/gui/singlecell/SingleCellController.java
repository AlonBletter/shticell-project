package gui.singlecell;

import dto.CellDTO;
import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import gui.app.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SingleCellController extends CellModel {
    private AppController mainController;

    @FXML private AnchorPane cellPane;
    @FXML private Label valueLabel;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void onCellClickUpdate(MouseEvent event) {
        if (mainController != null) {
            mainController.updateHeaderOnCellClick(coordinate);
        }
    }

    public SingleCellController() {
        super();
    }

    @FXML
    private void initialize() {
        valueLabel.textProperty().bind(effectiveValue);
    }

    public void setDataFromDTO(CellDTO cell) {
        String valueString = (cell != null ? formatEffectiveValue(cell.effectiveValue()) : "");

        setCoordinate(cell.coordinate());
        setEffectiveValue(valueString);
        setOriginalValue(cell.originalValue());
        setLastModifiedVersion(String.valueOf(cell.lastModifiedVersion()));
    }

    private String formatEffectiveValue(EffectiveValue effectiveValue) {
        String formattedObject = effectiveValue.getValue().toString();

        if(CellType.NUMERIC == effectiveValue.getCellType()) {
            double doubleValue = (Double) effectiveValue.getValue();

            if(doubleValue % 1 == 0) {
                long longValue = (long) doubleValue;

                formattedObject = String.format("%,d", longValue);
            } else {
                formattedObject = String.format("%,.2f", doubleValue);
            }
        } else if(CellType.BOOLEAN == effectiveValue.getCellType()) {
            boolean booleanValue = (Boolean) effectiveValue.getValue();

            formattedObject = Boolean.toString(booleanValue).toUpperCase();
        } else if (CellType.EMPTY == effectiveValue.getCellType()) {
            formattedObject = "";
        }

        return formattedObject;
    }
}