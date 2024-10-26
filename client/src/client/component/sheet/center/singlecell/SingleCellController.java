package client.component.sheet.center.singlecell;

import client.component.sheet.app.SheetController;
import client.component.sheet.center.CenterController;
import client.component.sheet.common.ShticellResourcesConstants;
import dto.CellDTO;
import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import javafx.animation.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;

public class SingleCellController extends CellModel {
    @FXML private AnchorPane cellPane;
    @FXML private Label valueLabel;

    private SheetController mainController;
    private CenterController centerController;
    private boolean editable;
    private final SimpleBooleanProperty textSpinAnimation = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty textFadeAnimation = new SimpleBooleanProperty(false);
    private RotateTransition rotateTransition;
    private ScaleTransition scaleTransition;
    private Timeline backgroundColorTimeline;
    private String effectiveValueToRestore;
    private boolean whatIfFlag = false;

    public SingleCellController() {
        super();
    }

    @FXML
    private void initialize() {
        valueLabel.textProperty().bind(effectiveValue);

        textSpinAnimation.addListener((observable, oldValue, newValue) -> {
            if (newValue && !getOriginalValue().isEmpty()) {
                rotateTransition = new RotateTransition(Duration.millis(1000), valueLabel);
                rotateTransition.setByAngle(360);
                rotateTransition.setCycleCount(Animation.INDEFINITE);
                rotateTransition.setInterpolator(Interpolator.EASE_BOTH);

                scaleTransition = new ScaleTransition(Duration.millis(1000), valueLabel);
                scaleTransition.setByX(0.5);
                scaleTransition.setByY(0.5);
                scaleTransition.setAutoReverse(true);
                scaleTransition.setCycleCount(Animation.INDEFINITE);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                backgroundColorTimeline = new Timeline(
                        new KeyFrame(Duration.millis(0), new KeyValue(cellPane.styleProperty(), "-fx-background-color: #ffcccb")),
                        new KeyFrame(Duration.millis(300), new KeyValue(cellPane.styleProperty(), "-fx-background-color: #ffffcc")),
                        new KeyFrame(Duration.millis(600), new KeyValue(cellPane.styleProperty(), "-fx-background-color: #ccffcc")),
                        new KeyFrame(Duration.millis(900), new KeyValue(cellPane.styleProperty(), "-fx-background-color: #ccffff")),
                        new KeyFrame(Duration.millis(1200), new KeyValue(cellPane.styleProperty(), "-fx-background-color: #e0ccff"))
                );
                backgroundColorTimeline.setCycleCount(Animation.INDEFINITE);
                backgroundColorTimeline.setAutoReverse(true);

                rotateTransition.play();
                scaleTransition.play();
                backgroundColorTimeline.play();

            } else if(!newValue && !getOriginalValue().isEmpty()) {
                rotateTransition.stop();
                scaleTransition.stop();
                backgroundColorTimeline.stop();
                valueLabel.setRotate(0);
                valueLabel.setScaleX(1);
                valueLabel.setScaleY(1);
                cellPane.setStyle(null);
            }
        });
    }

    public void clearSelection() {
        cellPane.getStyleClass().remove("selected-cell");
    }

    public void setMainController(SheetController mainController) {
        this.mainController = mainController;
        if (mainController != null) {
            textSpinAnimation.bind(mainController.getTextSpinAnimationProperty());
            textFadeAnimation.bind(mainController.textFadeAnimationProperty());
        }
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

    public void setAlignment(Pos targetPos) {
        valueLabel.setAlignment(targetPos);
    }

    public Pos getAlignment() {
        return valueLabel.getAlignment();
    }

    public void setCellText(String newText) {
        if (textFadeAnimation.getValue()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), valueLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                setEffectiveValue(newText);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), valueLabel);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        } else {
            setEffectiveValue(newText);
        }
    }

    public void setDataFromDTO(Coordinate coordinate, CellDTO cell) {
        setCoordinate(coordinate);

        if (cell != null) {
            setCellText(formatEffectiveValue(cell.effectiveValue()));
            setOriginalValue(cell.originalValue());
            setLastModifiedVersion(String.valueOf(cell.lastModifiedVersion()));
            updateBackgroundColor(cell.cellStyle().backgroundColor());
            updateTextColor(cell.cellStyle().textColor());
            setDependsOn(cell.dependsOn());
            setInfluenceOn(cell.influenceOn());
            setContainsFunction(cell.containsFunction());
            setCellType(cell.effectiveValue().cellType());
        } else {
            setCellText("");
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
                applyCSS(Objects.requireNonNull(ShticellResourcesConstants.BLUE_CELL_CSS_URL));
                break;
            case "Red":
                applyCSS(Objects.requireNonNull(ShticellResourcesConstants.RED_CELL_CSS_URL));
                break;
            case "Default":
                applyCSS(Objects.requireNonNull(ShticellResourcesConstants.DEFAULT_CELL_CSS_URL));
                break;
        }
    }

    private void applyCSS(URL cssURL) {
        cellPane.getStylesheets().clear();
        cellPane.getStylesheets().add(cssURL.toExternalForm());
    }

    public void setExpectedValue(EffectiveValue value) {
        if(!whatIfFlag) {
            effectiveValueToRestore = getEffectiveValue();
            whatIfFlag = true;
        }

        setEffectiveValue(formatEffectiveValue(value));
    }

    public void restoreEffectiveValue() {
        if (whatIfFlag) {
            whatIfFlag = false;
            setEffectiveValue(effectiveValueToRestore);
        }
    }
}