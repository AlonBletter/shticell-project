package gui.singlecell;

import dto.CellDTO;
import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CellModel {
    protected Coordinate coordinate;
    protected SimpleStringProperty originalValue;
    protected SimpleStringProperty effectiveValue;
    protected SimpleStringProperty lastModifiedVersion;

    public CellModel() {
        this.effectiveValue = new SimpleStringProperty();
        this.originalValue = new SimpleStringProperty();
        this.lastModifiedVersion = new SimpleStringProperty();
    }

    public String getEffectiveValue() {
        return effectiveValue.get();
    }

    public void setEffectiveValue(String effectiveValue) {
        this.effectiveValue.set(effectiveValue);
    }

    public StringProperty effectiveValueProperty() {
        return effectiveValue;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getOriginalValue() {
        return originalValue.get();
    }

    public SimpleStringProperty originalValueProperty() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue.set(originalValue);
    }

    public String getLastModifiedVersion() {
        return lastModifiedVersion.get();
    }

    public SimpleStringProperty lastModifiedVersionProperty() {
        return lastModifiedVersion;
    }

    public void setLastModifiedVersion(String lastModifiedVersion) {
        this.lastModifiedVersion.set(lastModifiedVersion);
    }
}
