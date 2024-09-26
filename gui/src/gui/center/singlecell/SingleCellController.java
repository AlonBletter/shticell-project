package gui.center.singlecell;

import dto.CellDTO;
import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import gui.app.AppController;
import gui.center.CenterController;
import gui.common.ShticellResourcesConstants;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;

public class SingleCellController extends CellModel {
    @FXML private AnchorPane cellPane;
    @FXML private Label valueLabel;

    private AppController mainController;
    private CenterController centerController;
    private boolean editable;

    public SingleCellController() {
        super();
    }

    @FXML
    private void initialize() {
        valueLabel.textProperty().bind(effectiveValue);
    }

    public void clearSelection() {
        cellPane.getStyleClass().remove("selected-cell");
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    public void setCenterController(CenterController centerController) {
        this.centerController = centerController;
    }

    @FXML
    public void onCellClickUpdate(MouseEvent event) {
        if (mainController != null) {
            centerController.updateDependenciesAndInfluences(dependsOn, influenceOn);
            mainController.setSelectedCell(this);
            cellPane.getStyleClass().add("selected-cell");
        }
    }

    public void setAlignment(Pos pos) {
        valueLabel.setAlignment(pos);
    }

    public Pos getAlignment() {
        return valueLabel.getAlignment();
    }

    public void setDataFromDTO(Coordinate coordinate, CellDTO cell) {
        setCoordinate(coordinate);

        if (cell != null) {
            setEffectiveValue(formatEffectiveValue(cell.effectiveValue()));
            setOriginalValue(cell.originalValue());
            setLastModifiedVersion(String.valueOf(cell.lastModifiedVersion()));
            updateBackgroundColor(cell.cellStyle().getBackgroundColor());
            updateTextColor(cell.cellStyle().getTextColor());
            setDependsOn(cell.dependsOn());
            setInfluenceOn(cell.influenceOn());
        } else {
            setEffectiveValue("");
            setOriginalValue("");
            setLastModifiedVersion("1");
        }
    }

    public Node getCellNode() {
        return cellPane;
    }

    private String formatEffectiveValue(EffectiveValue effectiveValue) {
        String formattedObject = effectiveValue.value().toString();

        if (CellType.NUMERIC == effectiveValue.cellType()) {
            double doubleValue = (Double) effectiveValue.value();

            if (doubleValue % 1 == 0) {
                long longValue = (long) doubleValue;

                formattedObject = String.format("%,d", longValue);
            } else {
                formattedObject = String.format("%,.2f", doubleValue);
            }
        } else if (CellType.BOOLEAN == effectiveValue.cellType()) {
            boolean booleanValue = (Boolean) effectiveValue.value();

            formattedObject = Boolean.toString(booleanValue).toUpperCase();
        } else if (CellType.EMPTY == effectiveValue.cellType()) {
            formattedObject = "";
        }

        return formattedObject;
    }

    public void updateBackgroundColor(String newColor) {
        if (newColor != null) {
            cellPane.setStyle("-fx-background-color: " + newColor);
        } else {
            cellPane.setStyle(null);
        }
    }

    public void updateTextColor(String newColor) {
        if (newColor != null) {
            valueLabel.setStyle("-fx-text-fill: " + newColor);
        } else {
            valueLabel.setStyle(null);
        }
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setSkin(String skinType) {
        switch (skinType) {
            case "Blue":
                applyCSS(ShticellResourcesConstants.BLUE_CELL_CSS_URL);
                break;
            case "Red":
                applyCSS(ShticellResourcesConstants.RED_CELL_CSS_URL);
                break;
            case "Default":
                applyCSS(ShticellResourcesConstants.DEFAULT_CELL_CSS_URL);
                break;
        }
    }

    private void applyCSS(URL cssURL) {
        cellPane.getStylesheets().clear();
        cellPane.getStylesheets().add(cssURL.toExternalForm());
    }
}