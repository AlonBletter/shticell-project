package client.component.sheet.center.singlecell;

import dto.cell.CellType;
import dto.coordinate.Coordinate;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.LinkedList;
import java.util.List;

public class CellModel {
    protected Coordinate coordinate;
    protected SimpleStringProperty originalValue;
    protected SimpleStringProperty effectiveValue;
    protected SimpleStringProperty lastModifiedVersion;
    protected SimpleStringProperty modifiedBy;
    protected List<Coordinate> dependsOn;
    protected List<Coordinate> influenceOn;
    protected boolean containsFunction;
    protected CellType cellType;

    public CellModel() {
        this.effectiveValue = new SimpleStringProperty();
        this.originalValue = new SimpleStringProperty();
        this.lastModifiedVersion = new SimpleStringProperty();
        this.modifiedBy = new SimpleStringProperty();
        this.dependsOn = new LinkedList<>();
        this.influenceOn = new LinkedList<>();
        this.containsFunction = false;
        this.cellType = CellType.EMPTY;
    }

    public String getModifiedBy() {
        return modifiedBy.get();
    }

    public SimpleStringProperty modifiedByProperty() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy.set(modifiedBy);
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

    public void setDependsOn(List<Coordinate> dependsOn) {
        this.dependsOn = new LinkedList<>(dependsOn);
    }

    public void setInfluenceOn(List<Coordinate> influenceOn) {
        this.influenceOn = new LinkedList<>(influenceOn);
    }

    public List<Coordinate> getDependsOn() {
        return dependsOn;
    }

    public List<Coordinate> getInfluenceOn() {
        return influenceOn;
    }

    public boolean isContainsFunction() {
        return containsFunction;
    }

    public void setContainsFunction(boolean containsFunction) {
        this.containsFunction = containsFunction;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }
}
